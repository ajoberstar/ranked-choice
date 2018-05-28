(ns ranked-choice.poll
  (:import [java.util UUID]))

(defonce polls (atom {}))

(defn list-polls []
  (map :id @polls))

(defn create-poll []
  (let [poll {:id (str (UUID/randomUUID))
              :candidates []
              :votes []
              :state :nomination}]
    (swap! polls assoc (:id poll) poll)
    poll))

(defn update-poll [])

(defn open-poll [])

(defn close-poll [])

(defn vote [])
