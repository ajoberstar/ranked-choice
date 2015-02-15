(ns ranked-choice.routes
  (:require [ranked-choice.voting :as voting]
            [ranked-choice.websocket :as websocket]
            [compojure.core :refer :all]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response redirect not-found]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.core.async :as async]
            [clojure.data.json :as json]))

(defn poll-routes
  [poll-ch]
  (routes
    (GET "/vote" []
         (resource-response "/vote.html"))
    (POST "/vote" [vote]
          (async/put! poll-ch {:vote vote}))
    (GET "/results" [socket :as request]
         (if socket
           (let [in-ch (async/chan (async/sliding-buffer 1))
                 out-ch (async/chan 1 (map json/write-str))]
             (async/put! poll-ch {:reply-ch out-ch})
             (websocket/websocket-handler in-ch out-ch))
           (resource-response "/results.html")))
    (GET "/close" []
         (async/close! poll-ch))))

(defroutes app-routes
  (GET "/" []
       (redirect "/poll/new"))
  (GET "/poll/new" []
       (resource-response "/new.html"))
  (POST "/poll/new" [candidates :as {poll-mgr :voting/poll-mgr}]
        (let [poll-id (voting/new-poll poll-mgr candidates)]
          (redirect (str "/poll/" poll-id "/vote"))))
  (context "/poll/:id" [id :as {poll-mgr :voting/poll-mgr}]
           (if-let [poll-ch (voting/get-poll-ch poll-mgr (Integer/parseInt id))]
             (poll-routes poll-ch)
             (not-found (str "No poll found with id: " id))))
  (resources ""))

(def app (wrap-params app-routes))
