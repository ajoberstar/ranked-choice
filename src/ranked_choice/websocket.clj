(ns ranked-choice.websocket
  (:require [org.httpkit.server :as httpkit]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]))

(defn- handle-close
  [http-ch in-ch out-ch]
  (httpkit/on-close http-ch
                    (fn [data]
                      (log/info "Closing " http-ch " because " data)
                      (async/close! in-ch)
                      (async/close! out-ch))))

(defn- handle-receive
  [http-ch in-ch]
  (httpkit/on-receive http-ch
                      (partial async/put! in-ch)))

(defn- handle-send
  [http-ch out-ch]
  (async/thread
    (loop []
      (when-let [data (async/<!! out-ch)]
        (httpkit/send! http-ch data)
        (recur)))))

(defn websocket-handler
  [in-ch out-ch]
  (fn [request]
    (httpkit/with-channel request channel
      (log/info "Connecting to " channel)
      (handle-close channel in-ch out-ch)
      (handle-receive channel in-ch)
      (handle-send channel out-ch))))
