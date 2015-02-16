(ns ranked-choice.routes
  (:require [ranked-choice.voting :as voting]
            [ranked-choice.websocket :as websocket]
            [compojure.core :refer :all]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response redirect not-found]]
            [ring.middleware.params :refer [wrap-params]]
            [net.cgrand.enlive-html :as html]
            [clojure.core.async :as async]
            [clojure.data.json :as json]))

(html/deftemplate vote "vote.html" [id candidates]
  [:li.candidate] (html/clone-for [candidate candidates] (html/content candidate))
  [:#abstain-btn] (html/set-attr :href (str "/poll/" id "/results") ))

(defn poll-routes
  [poll id]
  (routes
    (GET "/vote" []
         (vote id (:candidates poll)))
    (POST "/vote" [vote]
          (let [vote-coll (if (instance? String vote) [vote] vote)]
            (async/put! (:poll-ch poll) {:vote vote-coll})
            (redirect (str "/poll/" id "/results"))))
    (GET "/results" [socket :as request]
         (if socket
           (let [in-ch (async/chan (async/sliding-buffer 1))
                 out-ch (async/chan 1 (map json/write-str))]
             (async/put! (:poll-ch poll) {:reply-ch out-ch})
             (websocket/websocket-handler in-ch out-ch))
           (resource-response "/results.html")))
    (GET "/close" []
         (async/close! (:poll-ch poll)))))

(defroutes app-routes
  (GET "/" []
       (redirect "/poll/new"))
  (GET "/poll/new" []
       (resource-response "/new.html"))
  (POST "/poll/new" [candidates :as {poll-mgr :voting/poll-mgr}]
        (let [candidates-coll (if (instance? String candidates) [candidates] candidates)
              poll-id (voting/new-poll poll-mgr candidates-coll)]
          (redirect (str "/poll/" poll-id "/vote"))))
  (context "/poll/:id" [id :as {poll-mgr :voting/poll-mgr}]
           (if-let [poll (voting/get-poll poll-mgr (Integer/parseInt id))]
             (poll-routes poll id)
             (not-found (str "No poll found with id: " id))))
  (resources ""))

(def app (wrap-params app-routes))
