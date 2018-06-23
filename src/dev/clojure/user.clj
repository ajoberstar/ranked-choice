(ns user
  (:require [mount.core :as mount]
            [clojure.tools.namespace.repl :as tn]
            [gradle-clojure.tools.figwheel :as fw]))

(defn dev []
  (in-ns 'dev))

(defn reset []
  (mount/stop)
  (tn/refresh :after 'dev/go))
