(ns ranked-choice.core
  (:require [com.stuartsierra.component :as component]
            [ranked-choice.httpkit :as httpkit]
            [ranked-choice.routes :as routes]
            [ranked-choice.poll :as poll])
  (:gen-class))

(defn system
  [httpkit-opts]
  (component/system-map
    :poll/poll-mgr (poll/map->PollManager {})
    :handler (component/using {:handler-fn routes/app} [:poll/poll-mgr])
    :server (component/using
              (httpkit/map->HttpKitServer {:options httpkit-opts})
              [:handler])))

(defn -main [& args]
  (let [port (some-> args first Integer/parseInt)
        opts (some->> port (conj [:port]) (apply hash-map))]
    (-> opts system component/start-system)))
