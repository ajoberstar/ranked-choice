(ns user
  (:require [mount.core :as mount]
            [clojure.tools.namespace.repl :as tn]))

(defn dev []
  (in-ns 'dev))

(defn reset []
  (mount/stop)
  (tn/refresh :after 'dev/go))
