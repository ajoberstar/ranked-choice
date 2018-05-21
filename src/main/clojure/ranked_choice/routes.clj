(ns ranked-choice.routes
  (:require [bidi.ring :as bidi]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defn ok [request]
  (resp/response {:status "OK"}))

(defn index [_]
  (resp/redirect "index.html"))

(def route ["/" {"" index
                 "api/" ok}])

(def site
  (-> route
      (bidi/make-handler)
      (wrap-json-response)
      (wrap-defaults site-defaults)))
