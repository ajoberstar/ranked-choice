(ns ranked-choice.main
  (:require [mount.core :as mount :refer [defstate]]
            [org.httpkit.server :as http-kit]
            [ranked-choice.routes :as routes]))

(defstate server
  :start (http-kit/run-server routes/site {:port 8080})
  :stop (server))

(defn -main [& args])
