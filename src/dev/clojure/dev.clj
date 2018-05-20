(ns dev
  (:require [mount.core :as mount :refer [defstate]]
            [clojure.tools.namespace.repl :as repl]
            [org.httpkit.server :as http-kit]))

(defn start! [] (println "Starting stupid!"))
(defn stop! [] (println "Stopping stupid!"))

(defstate stupid
  :start (start!)
  :stop (stop!))

(defn ok [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "\"Http Kit 2!\""})

(defstate server
  :start (http-kit/run-server ok {:port 8080})
  :stop (server))

(defn go []
  (mount/start)
  :ready)

(defn reset []
  (mount/stop)
  (repl/refresh :after 'dev/go))
