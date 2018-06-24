(ns ranked-choice.frontend.main
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [goog.dom :as gdom]))

(def app-state (atom {:count 0}))

(defui Counter
  Object
  (render [this]
          (let [{:keys [count]} (om/props this)]
            (dom/div nil
                     (dom/span nil (str "Count: " count))
                     (dom/button #js {:onClick (fn [e]
                                                 (swap! app-state update-in [:count] inc))}
                                 "Click me!")))))

(def reconciler (om/reconciler {:state app-state}))

(om/add-root! reconciler
              Counter (gdom/getElement "app"))

; (defui HelloWorld
;   Object
;   (render [this]
;           (let [{:keys [title]} (om/props this)]
;             (dom/h1 nil title))))

; (def hello-world (om/factory HelloWorld))

; (js/ReactDOM.render (apply dom/div nil (map #(hello-world {:react-key % :title (str "Hi, " % " times")}) (range 10))) (gdom/getElement "app"))
