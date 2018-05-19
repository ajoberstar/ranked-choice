(ns ranked-choice.httpkit
  (:require [org.httpkit.server :as httpkit]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]))

(defn- handle-close
  "Registers an on-close function with an http-kit channel.
  This will close both the input and output core.async channels
  when the http-kit channel closes."
  [http-ch in-ch out-ch]
  (httpkit/on-close http-ch
                    (fn [data]
                      (log/debug "Closing " http-ch " because " data)
                      (async/close! in-ch)
                      (async/close! out-ch))))

(defn- handle-receive
  "Registers an on-receive function with an http-kit channel.
  This asynchronously puts any received message onto the input
  core.async channel."
  [http-ch in-ch]
  (httpkit/on-receive http-ch
                      (partial async/put! in-ch)))

(defn- handle-send
  "Handles all messages from the output core.async channel and
  sends them to the http-kit channel."
  [http-ch out-ch]
  (async/thread
    (loop []
      (when-let [data (async/<!! out-ch)]
        (httpkit/send! http-ch data)
        (recur)))))

(defn websocket-handler
  "Creates a Ring handler that uses http-kits with-channel
  handler to pipe all received messages to the provided
  core.async in-ch and send any messages taken off the
  core.async out-ch to the http-kit socket."
  [in-ch out-ch]
  (fn [request]
    (httpkit/with-channel request channel
      (log/debug "Connecting to " channel)
      (handle-close channel in-ch out-ch)
      (handle-receive channel in-ch)
      (handle-send channel out-ch))))

(defn- wrap-component [handler [key component]]
  "Ring middleware to add a component to the request
  map using the given key."
  (fn [request]
    (handler (assoc request key component))))

(defn- make-handler [handler]
  "Creates a handler function from the handler map/component.
  The :handler-fn key should be a normal Ring handler. Any additional
  keys will be used in middleware to add them to the request."
  (let [handler-fn (:handler-fn handler)
        components (dissoc handler :handler-fn)]
    (reduce wrap-component handler-fn components)))

(defrecord HttpKitServer [handler options stop-fn]
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
