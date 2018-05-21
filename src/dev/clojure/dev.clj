(ns dev
  (:require [mount.core :as mount :refer [defstate]]
            [clojure.tools.namespace.repl :as repl]))

(defn go []
  (mount/start)
  :ready)

(defn reset []
  (mount/stop)
  (repl/refresh :after 'dev/go))
