(ns ranked-choice.core
  (:require [com.stuartsierra.component :as component]
            [ranked-choice.server :as server]
            [ranked-choice.routes :as routes]))

(defn http-server []
  (server/map->Server {:handler-fn routes/app
                       :options {}}))

(defn dev-system []
  (component/system-map
    :server (http-server)))

(defn prod-system []
  (component/system-map
    :server (http-server)))

(defn -main [& args]
  (component/start-system (dev-system)))
