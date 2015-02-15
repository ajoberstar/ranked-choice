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
           (POST "/new" [candidates :as {races :vote/races}]
                 (vote/new-poll races candidates))
           (GET "/vote/:id" [id]
                (resource-response "/vote.html"))
           (resources ""))

(def app (wrap-params app-routes))
