(ns ranked-choice.voting.rcv
  (:require [ranked-choice.voting :as voting]
            [clojure.core.async :as async]
            [ranked-choice.transducers :as xf]))

(defn tally-xf [tally]
  (map (fn [ballots]
         (->> ballots
              (map tally)
              (apply merge-with +)
              (assoc {:ballots ballots} :tally)))))

(defrecord RankedChoice
  [tally no-winner? runoff]
  voting/VotingSystem
  (count-ballots [vsys candidates ballots]
    (let [base-results (zipmap candidates (repeat []))
          base-tally (zipmap candidates (repeat 0))
          {:keys [tally no-winner? runoff]} vsys
          votes-ch (async/chan 1 (comp (dedupe)
                                       (tally-xf tally)))
          runoff-ch (async/chan 1 (comp (take-while no-winner?)
                                        (map runoff)))
          results-ch (async/chan 1 (comp (map :tally)
                                         (map #(into base-tally %))))
          tally-mult (async/mult votes-ch)]
      (async/tap tally-mult runoff-ch)
      (async/tap tally-mult results-ch)
      (async/pipe runoff-ch votes-ch)
      (async/put! votes-ch ballots)
      (let [results (async/reduce #(merge-with conj %1 %2) base-results results-ch)]
        (async/<!! results)))))

(defn- first-count [ballot] {(first ballot) 1})

(defn- bucklin-count [_] nil)

(defn- borda-count [ballot]
  (->> (range)
       (drop 1)
       (zipmap (reverse ballot))))

(defn- nauru-count [ballot]
  (->> (range)
       (drop 1)
       (map #(/ 1 %))
       (zipmap ballot)))

(defn- no-majority?
  [{:keys [tally]}]
  (let [totals (map last tally)
        votes-needed (/ (reduce + totals) 2)]
    (every? #(< % votes-needed) totals)))

(defn- challenger?
  [{:keys [ballots]}]
  (some #(->> % count (< 1)) ballots))

(def ^:private one-round (constantly false))

(defn- runoff-below
  [ballots tally threshold]
  (let [pred (comp #(<= % threshold) last)
        losers (->> tally (filter pred) (map first) (into #{}))]
    (map (partial remove losers) ballots)))

(defn runoff-but-last
  [{:keys [tally ballots]}]
  (->> tally (map last) (reduce min) (runoff-below ballots tally)))

(defn- runoff-avg
  [{:keys [tally ballots]}]
  (let [totals (map last tally)
        avg (/ (reduce + totals) (count totals))]
    (runoff-below ballots tally avg)))

(defn- runoff-coombs [_] nil)

(defn- runoff-everyone [{:keys [ballots]}] ballots)

(def systems {"irv" (->RankedChoice first-count no-majority? runoff-but-last)
              "borda" (->RankedChoice borda-count one-round runoff-everyone)
              "nauru" (->RankedChoice nauru-count one-round runoff-everyone)
              "nanson" (->RankedChoice borda-count challenger? runoff-avg)
              "baldwin" (->RankedChoice borda-count challenger? runoff-but-last)})

;; (def coombs (->RankedChoice first-count no-majority? runoff-coombs))
;; (def bucklin (->RankedChoice bucklin-count no-majority? runoff-everyone))
