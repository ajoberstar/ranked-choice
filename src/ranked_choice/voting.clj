(ns ranked-choice.voting
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]))

(defrecord Poll [candidates poll-ch])

(defrecord PollManager [polls]
  component/Lifecycle
  (start [component]
    (assoc component :polls (atom [])))
  (stop [component]
    (swap! (:polls component)
           (fn [polls]
             (doseq [poll polls]
               (async/close! (:poll-ch poll)))))
    (dissoc component :polls)))

(defn get-poll
  [poll-mgr poll-id]
  (-> poll-mgr
      :polls
      deref
      (nth poll-id nil)))

(defn- tabulate
  [votes]
  (let [candidates (first votes)
        base-results (zipmap candidates (repeat 0))]
    (->> votes
         (map first)
         frequencies
         (into base-results))))

(defn- remove-candidate
  [votes candidate]
  (map (fn [vote]
         (remove #(= % candidate) vote))
       votes))

(defn- empty-results
  [candidates]
  (zipmap candidates (repeat [])))

(defn- format-results
  [results]
  (->> results
       (map (fn [[candidate votes]]
              (into [candidate] votes)))
       (sort-by (comp #(into [] %) reverse))
       reverse
       (into [])))

(defn- count-votes
  [votes]
  (let [candidates (first votes)
        votes-to-win (/ (count votes) 2)
        base-results (zipmap candidates (repeat 0))]
    (loop [remaining-votes votes
           prev-results (empty-results candidates)]
      (let [round-results (tabulate remaining-votes)
            new-results (->> round-results
                             (into base-results)
                             (merge-with conj prev-results))
            most-votes (reduce max (vals round-results))
            last-place (first (apply min-key last round-results))]
        (if (>= most-votes votes-to-win)
          new-results
          (recur (remove-candidate remaining-votes last-place) new-results))))))

(defn- handle-poll
  [msg old-votes old-results]
  (if-let [vote (:vote msg)]
    (let [new-votes (conj old-votes vote)
          new-results (-> new-votes count-votes format-results)]
      [new-votes new-results])
    [old-votes old-results]))

(defn new-poll
  [poll-mgr candidates]
  (let [poll-ch (async/chan)
        results-ch (async/chan)
        results-mult (async/mult results-ch)]
    (async/go-loop [old-votes []
                    old-results []]
      (if-let [msg (async/<! poll-ch)]
        (do
          (log/info "Received " msg)
          (some->> (:reply-ch msg) (async/tap results-mult))
          (let [[new-votes new-results] (handle-poll msg old-votes old-results)]
            (log/info "Votes: " new-votes)
            (log/info "Results: " new-results)
            (async/>! results-ch new-results)
            (recur new-votes new-results)))
        (async/close! results-ch)))
    (-> poll-mgr
        :polls
        (swap! conj (->Poll candidates poll-ch))
        count
        dec)))
