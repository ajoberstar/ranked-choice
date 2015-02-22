(ns ranked-choice.poll
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            [ranked-choice.voting :as voting]
            [ranked-choice.transducers :as xf]))

(defrecord Poll [candidates
                 votes-ch
                 results-mult
                 latest-results])

(defn new-poll
  [vsys candidates]
  (let [latest-results (atom [])
        results-xf (comp (xf/reductions conj)
                         (map (voting/results vsys))
                         (xf/peek (partial reset! latest-results)))
        votes-ch (async/chan 10)
        results-mult (->> (async/chan 1 results-xf) (async/pipe votes-ch) async/mult)]
    (->Poll candidates votes-ch results-mult latest-results)))

(defn pipe-results
  [poll ch]
  (async/put! ch @(:latest-results poll))
  (async/tap (:results-mult poll) ch)
  ch)

(defrecord PollManager [polls]
  component/Lifecycle
  (start [component]
    (assoc component :polls (atom [])))
  (stop [component]
    (swap! (:polls component)
           (fn [polls]
             (doseq [poll (keep identity polls)]
               (async/close! (:votes-ch poll)))))
    (dissoc component :polls)))

(defn get-poll
  [poll-mgr poll-id]
  (-> poll-mgr :polls deref (nth poll-id nil)))

(defn conj-poll
  [poll-mgr poll]
  (-> poll-mgr :polls (swap! conj poll) count dec))
