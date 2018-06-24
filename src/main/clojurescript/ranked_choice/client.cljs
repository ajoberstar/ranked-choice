(ns ranked-choice.client
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defn dispatch-timer-event []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))

; (defonce do-timer (js/setInterval dispatch-timer-event 5000))

(rf/reg-event-db
 :timer
 (fn [db [_ new-time]]
   (assoc db :time new-time)))


(rf/reg-event-db
 :initialize
 (fn [db _]
   (assoc db :time (js/Date.))))


(rf/reg-sub
 :time
 (fn [db _]
   (:time db)))

(defn greet []
  [:p (str "Hello! It's not " @(rf/subscribe [:time]))])


(rf/dispatch-sync [:initialize])

(r/render [greet] (.getElementById js/document "app"))
