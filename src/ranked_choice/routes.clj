(ns ranked-choice.routes
  (:require [ranked-choice.vote :as vote]
            [compojure.core :refer :all]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response redirect]]
            [ring.middleware.params :refer [wrap-params]]))

(defroutes app-routes
           (GET "/" []
                (redirect "/new"))
           (GET "/new" []
                (resource-response "/new.html"))
           (POST "/new" [candidates]
                 (if-let [id (vote/new-poll candidates)]
                   {:status 201
                    :location (str "/vote/" id)}
                   {:status 400
                    :body "Failed to open polls."}))
           (GET "/vote/:id" [id]
                (resource-response "/vote.html"))
           (resources ""))

(def app (wrap-params app-routes))
