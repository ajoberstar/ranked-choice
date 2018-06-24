(ns ranked-choice.frontend.main
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [goog.dom :as gdom]))

(def app-state (atom {:count 0}))

(defn read [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ v] (find st key)]
      {:value v}
      {:value :not-found})))

(defn mutate [{:keys [state] :as env} key params]
  (if (= 'increment key)
    {:value {:keys [:count]}
     :action #(swap! state update-in [:count] inc)}
    {:value :not-found}))

(defui Counter
  static om/IQuery
  (query [this]
         [:count])
  Object
  (render [this]
          (let [{:keys [count]} (om/props this)]
            (dom/div nil
                     (dom/span nil (str "Count: " count))
                     (dom/button #js {:onClick (fn [e]
                                                 (om/transact! this '[(increment)]))}
                                 "Click me!")))))

(def reconciler (om/reconciler {:state app-state
                                :parser (om/parser {:read read :mutate mutate})}))

(om/add-root! reconciler
              Counter (gdom/getElement "app"))
