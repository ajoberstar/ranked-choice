(ns ranked-choice.routes
  (:require [bidi.bidi :as bidi]
            [bidi.ring :refer [make-handler]]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]))

(declare route)

(defn ok [request]
  (resp/response {:status "OK"}))

(defn index [_]
  (resp/redirect "index.html"))

(defn poll-view [request]
  (resp/response ["Calvin" "Hobbes" "Locke"]))

(defn polls-list [request]
  (->> [123 456 789]
       (map #(bidi/path-for route poll-view :id %))
       (into [])
       (resp/response)))

(def route ["/" {"" index
                 "api/poll/" {"" polls-list
                              [:id] poll-view}}])

(def site
  (-> route
      (make-handler)
      (wrap-json-response)
      (wrap-defaults site-defaults)))
