(ns ranked-choice.main
  (:require [mount.core :as mount]
            [ranked-choice.server :as server])
  (:gen-class))

(defn parse-args [args]
  (let [[port & rest] args]
    {:http-port (Integer/parseInt port)}))

(defn -main [& args]
  (mount/start-with-args
    (parse-args args)))
