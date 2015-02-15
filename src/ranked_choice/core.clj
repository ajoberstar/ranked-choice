(ns ranked-choice.core
  (:require [com.stuartsierra.component :as component]
            [ranked-choice.server :as server]
            [ranked-choice.routes :as routes]
            [ranked-choice.vote :as vote]))

(defn system
  [httpkit-opts]
  (component/system-map
    :races (vote/map->Races {})
    :handler (component/using {:handler-fn routes/app} {:vote/races :races})
    :server (component/using
              (server/map->Server {:options httpkit-opts})
              [:handler])))

(defn -main [& args]
  (component/start-system (system {})))
