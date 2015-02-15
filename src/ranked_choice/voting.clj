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

(defn- count-votes
  [votes]
  [["Teddy" 15 19 21]
   ["Abe" 10 15 16]
   ["George" 5 0 0]])

(defn- handle-poll
  [msg old-votes old-results]
  (if-let [vote (:vote msg)]
    (let [new-votes (conj old-votes vote)
          new-results (count-votes new-votes)]
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
