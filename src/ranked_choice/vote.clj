(ns ranked-choice.vote
  (:require [com.stuartsierra.component :as component]))

(defrecord Voter
  [socket name rankings])

(defrecord Race
  [candidates voters])

(defrecord Races [races]
  component/Lifecycle
  (start [component]
    (assoc component :races (agent {})))
  (stop [component]
    (dissoc component :races)))

(defn new-poll
  [races candidates]
  (str races candidates))
