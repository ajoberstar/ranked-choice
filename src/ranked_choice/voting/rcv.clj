(ns ranked-choice.voting.rcv
  (:require [ranked-choice.voting :as voting]
            [clojure.core.async :as async]))

(defrecord RankedChoice
  [tally winner? runoff]
  voting/VotingSystem
  (count-ballots [vsys candidates ballots]
    (let [{:keys [tally no-winner? runoff]} vsys
          votes-ch (async/chan 1 (map tally))
          runoff-ch (async/chan 1 (comp (take-while no-winner?)
                                        (map runoff)))
          results-ch (async/chan 1 (comp (map :tally)))
          tally-mult (async/mult votes-ch)]
      (async/tap tally-mult runoff-ch)
      (async/tap tally-mult results-ch)
      (async/pipe runoff-ch votes-ch)
      (async/put! votes-ch ballots)
      (async/into [] results-ch))))

(defn- first-count [vote] {(first vote) 1})
(defn- bucklin-count [_] nil)
(defn- borda-count [_] nil)
(defn- nauru-count [_] nil)

(defn- no-majority?
  [{:keys [tally]}]
  (let [totals (map last tally)
        votes-needed (/ (reduce + totals) 2)]
    (some #(> % votes-needed) totals)))

(defn- challenger?
  [{:keys [tally]}]
  (->> tally (map last) (filter pos?) (count) (= 1)))

(def ^:private one-round (constantly false))

(defn- runoff-below
  [votes tally threshold]
  (let [pred (comp #(<= % threshold) last)
        losers (->> tally (filter pred) (map first) (into #{}))]
    (map (partial remove losers) votes)))

(defn- runoff-but-last
  [{:keys [tally votes]}]
  (->> tally (map last) (reduce min) (runoff-below votes tally)))

(defn- runoff-avg
  [{:keys [tally votes]}]
  (let [totals (map last tally)
        avg (/ (reduce + totals) (count totals))]
    (runoff-below votes tally avg)))

(defn- runoff-coombs [_] nil)

(defn- runoff-everyone [{:keys [votes]}] votes)

(def irv (->RankedChoice first-count no-majority? runoff-but-last))
(def coombs (->RankedChoice first-count no-majority? runoff-coombs))
(def bucklin (->RankedChoice bucklin-count no-majority? runoff-everyone))
(def borda (->RankedChoice borda-count one-round runoff-everyone))
(def nauru (->RankedChoice nauru-count one-round runoff-everyone))
(def nanson (->RankedChoice borda-count challenger? runoff-avg))
(def baldwin (->RankedChoice borda-count challenger? runoff-but-last))
