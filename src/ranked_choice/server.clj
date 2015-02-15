(ns ranked-choice.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]))

(defn- wrap-component [handler [key component]]
  (fn [request]
    (handler (assoc request key component))))

(defn- make-handler [handler]
  (let [handler-fn (:handler-fn handler)
        components (dissoc handler :handler-fn)]
    (reduce wrap-component handler-fn components)))

(defrecord Server [handler options stop-fn]
  component/Lifecycle
  (start [server]
    (if (:stop-fn server)
      server
      (let [{:keys [handler options]} server
            handler-fn (make-handler handler)
            stop-fn (httpkit/run-server handler-fn options)]
        (assoc server :stop-fn stop-fn))))
  (stop [server]
    (if-let [stop-fn (:stop-fn server)]
      (do
        (stop-fn)
        (dissoc server :stop-fn))
      server)))
