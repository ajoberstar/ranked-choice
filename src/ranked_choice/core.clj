(ns ranked-choice.core
  (:require [com.stuartsierra.component :as component]
            [ranked-choice.server :as server]
            [ranked-choice.routes :as routes]
            [ranked-choice.voting :as voting])
  (:gen-class))

(defn system
  [httpkit-opts]
  (component/system-map
    :voting/poll-mgr (voting/map->PollManager {})
    :handler (component/using {:handler-fn routes/app} [:voting/poll-mgr])
    :server (component/using
              (server/map->Server {:options httpkit-opts})
              [:handler])))

(defn -main [& args]
  (let [port (some-> args first Integer/parseInt)
        opts (some->> port (conj [:port]) (apply hash-map))]
    (-> opts system component/start-system)))
