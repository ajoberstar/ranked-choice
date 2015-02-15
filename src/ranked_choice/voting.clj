(ns ranked-choice.voting
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(defrecord PollManager [poll-chs]
  component/Lifecycle
  (start [component]
    (assoc component :poll-chs (atom [])))
  (stop [component]
    (swap! (:poll-chs component)
           (fn [poll-chs]
             (doall (filter async/close! poll-chs))))
    (dissoc component :poll-chs)))

(defn get-poll-ch
  [poll-mgr poll-id]
  (-> poll-mgr
      :poll-chs
      deref
      (nth poll-id nil)))

(defn- count-votes
  [votes]
  votes)

(defn- handle-poll
  [msg old-votes old-results]
  (if-let [vote (:vote msg)]
    (let [new-votes (conj old-votes vote)
          new-results (count-votes new-votes)]
      [new-votes new-results])
    [old-votes old-results]))

(defn new-poll
  [poll-mgr candidates]
  (let [poll-ch (async/chan)]
    (async/go-loop [old-votes []
                    old-results []
                    results-ch (async/chan)
                    results-mult (async/mult results-ch)]
      (if-let [msg (async/<! poll-ch)]
        (do
          (some->> (:reply-ch msg) (async/tap results-mult))
          (let [[new-votes new-results] (handle-poll msg old-votes old-results)]
            (async/>! results-ch new-results)
            (recur new-votes new-results results-ch results-mult)))
        (async/close! results-ch)))
    (-> poll-mgr
        :poll-chs
        (swap! conj poll-ch)
        count
        dec)))
