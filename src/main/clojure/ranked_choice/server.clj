(ns ranked-choice.server
  (:require [mount.core :as mount :refer [defstate]]
            [ring.adapter.jetty :as jetty]))

(defn ok [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "\"200 OK\""})

(defn start-server []
  (jetty/run-jetty ok {:port 8080
                       :join? false
                       :daemon true}))
(defstate server
  :start (start-server)
  :stop (.stop server))
