(ns ranked-choice.routes
  (:require [compojure.core :refer :all]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [redirect]]
            [ring.middleware.params :refer [wrap-params]]))

(defroutes app-routes
           (GET "/" []
                (redirect "/new.html"))
           (GET "/vote/:id" [id]
                (str "Vote ID: " id))
           (resources ""))

(def app (wrap-params app-routes))
