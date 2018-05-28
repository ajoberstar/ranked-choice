(ns ranked-choice.routes
  (:require [ranked-choice.poll :as poll]
            [bidi.bidi :as bidi]
            [bidi.ring :refer [make-handler resources]]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(declare route)

(defn ok [request]
  (resp/response {:status "OK"}))

(defn index [request]
  (->
    (resp/resource-response "index.html" {:root "public"})
    (resp/content-type "text/html")))

(defn get-poll [request]
  (resp/response ["Calvin" "Hobbes" "Locke"]))

(defn create-poll [request]
  (let [poll (poll/create-poll)]
    (bidi/path-for route get-poll :id (:id poll))))

(defn get-all-polls [request]
  (->> (poll/list-polls)
       (map #(bidi/path-for route get-poll :id %))
       (into [])
       (resp/response)))

(defn list-candidates [request])

(defn update-candidates [request])

(defn list-votes [request])

(defn add-vote [request])

(defn update-poll [request])

(def route
  ["/" {"" index
        "api/" {"" {:get get-all-polls
                    :post create-poll}
                [:id] {"" {:get get-poll}
                       "candidates/" {:get list-candidates
                                      :put update-candidates}
                       "votes/" {:get list-votes
                                 :post add-vote}
                       :patch update-poll}}}])

(def site
  (-> route
      (make-handler)
      (wrap-json-body)
      (wrap-json-response)
      (wrap-defaults api-defaults)))
