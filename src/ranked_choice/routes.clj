(ns ranked-choice.routes
  (:require [ranked-choice.httpkit :as httpkit]
            [compojure.core :refer :all]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response redirect not-found]]
            [ring.middleware.params :refer [wrap-params]]
            [net.cgrand.enlive-html :as html]
            [clojure.core.async :as async]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [ranked-choice.poll :as poll]
            [ranked-choice.voting.rcv :as rcv]))

(html/deftemplate vote "vote.html" [id candidates]
  [:li.candidate] (html/clone-for [candidate candidates] (html/content candidate))
  [:#abstain-btn] (html/set-attr :href (str "/poll/" id "/results") ))

(html/deftemplate monitor "monitor.html" [polls-by-id]
  [:div.poll] (html/clone-for [[id poll] polls-by-id]
                             [:.candidates] (->> poll :candidates (string/join ", ") html/content)
                             [:a] (html/set-attr :href (str "/poll/" id "/close"))))

(defn poll-routes
  [poll-mgr poll id]
  (routes
    (GET "/vote" []
         (vote id (-> poll :candidates shuffle)))
    (POST "/vote" [vote]
          (let [vote-coll (if (instance? String vote) [vote] vote)]
            (async/put! (:votes-ch poll) vote-coll)
            (redirect (str "/poll/" id "/results"))))
    (GET "/results" [socket :as request]
         (if socket
           (let [in-ch (async/chan (async/sliding-buffer 3))
                 out-ch (async/chan (async/sliding-buffer 3) (map json/write-str))]
             (poll/pipe-results poll out-ch)
             (httpkit/websocket-handler in-ch out-ch))
           (resource-response "/results.html")))
    (GET "/close" []
         (async/close! (:votes-ch poll))
         (swap! (:polls poll-mgr) assoc id nil)
         (redirect "/poll/monitor"))))

(defroutes app-routes
  (GET "/" []
       (redirect "/poll/new"))
  (GET "/poll/new" []
       (resource-response "/new.html"))
  (POST "/poll/new" [candidates vsys :as {poll-mgr :poll/poll-mgr}]
        (let [candidates-coll (if (instance? String candidates) [candidates] candidates)
              poll (poll/new-poll (get rcv/systems vsys) candidates-coll)
              poll-id (poll/conj-poll poll-mgr poll)]
          (redirect (str "/poll/" poll-id "/vote"))))
  (GET "/poll/monitor" {poll-mgr :poll/poll-mgr}
       (let [polls @(:polls poll-mgr)
             polls-by-id (->> polls (map-indexed vector) (filter last))]
         (monitor polls-by-id)))
  (context "/poll/:id" [id :as {poll-mgr :poll/poll-mgr}]
           (let [poll-id (Integer/parseInt id)]
             (if-let [poll (poll/get-poll poll-mgr poll-id)]
               (poll-routes poll-mgr poll poll-id)
               (not-found (str "No poll found with id: " poll-id)))))
  (resources ""))

(def app (wrap-params app-routes))
