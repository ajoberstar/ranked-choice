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
  "Creates a new poll using the provided VotingSystem
  vsys and set of candidates. After creation voters
  can cast votes by putting them on the :votes-ch
  of the poll. Callers interested in the results of
  the poll should use pipe-results to have the results
  added to their channel. Returns the new poll."
  [vsys candidates]
  (let [latest-results (atom [])
        results-xf (comp (xf/reductions conj)
                         (map (voting/results vsys candidates))
                         (xf/peek (partial reset! latest-results)))
        votes-ch (async/chan 10)
        results-mult (->> (async/chan 1 results-xf) (async/pipe votes-ch) async/mult)]
    (->Poll candidates votes-ch results-mult latest-results)))

(defn pipe-results
  "Pipes the results of the given poll onto the
  channel ch. The latest results are always provided
  first. Subsequent results will be provided as additional
  votes are cast. Returns the provided channel."
  [poll ch]
  (async/put! ch @(:latest-results poll))
  (async/tap (:results-mult poll) ch)
  ch)

(def ^:private close-all!
  (comp (map :votes-ch)
        (filter identity)
        (map async/close!)))

(defrecord PollManager [polls]
  component/Lifecycle
  (start [component]
    (if (:polls component)
      component
      (assoc component :polls (atom []))))
  (stop [component]
    (if (:polls component)
      (do
        (swap! (:polls component)
               #(into [] close-all! %))
        (dissoc component :polls))
      component)))

(defn get-poll
  "Gets a poll from the provided manager by id."
  [poll-mgr poll-id]
  (-> poll-mgr :polls deref (nth poll-id nil)))

(defn conj-poll
  "Conj's a poll onto the manager and returns its id."
  [poll-mgr poll]
  (-> poll-mgr :polls (swap! conj poll) count dec))
