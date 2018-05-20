(ns user
  (:require [ranked-choice.main]
            [mount.core :as mount]
            [clojure.tools.namespace.repl :as tn]))

(defn go []
  (mount/start-with-args
    {:http-port 8888})
  :ready)

(defn reset []
  (mount/stop)
  (tn/refresh :after 'user/go))
