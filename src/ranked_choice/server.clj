(ns ranked-choice.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]))

(defrecord Server [handler-fn options stop-fn]
  component/Lifecycle
  (start [server]
    (let [{:keys [handler-fn options]} server]
      (assoc server :stop-fn (httpkit/run-server handler-fn options))))
  (stop [server]
    (let [{:keys [stop-fn]} server]
      (stop-fn)
      (dissoc server :stop-fn))))
