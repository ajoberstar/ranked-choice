(ns org.ajoberstar.ranked-choice
  
  (:require [reagent.core :as reagent]
            [re-frame.core :as reframe]))

(enable-console-print!)

(println "It works! It really works!")

(defn body-component []
  [:div
    [:div {:class "mdl-layout mdl-js-layout mdl-layout--fixed-header"}
      [:header {:class "mdl-layout__header"}
        [:div {:class "mdl-layout__header-row"}
          [:span {:class "mdl-layout-title"} "Ranked Choice"]
          [:div {:class "mdl-layout-spacer"}]
          [:nav {:class "mdl-navigation mdl-layout--large-screen-only"}
            [:a {:class "mdl-navigation__link" :href "yo"} "Link"]]]]]
    [:main {:class "mdl-layout__content"}
      [:div {:class "mdl-grid"}
        [:div {:class "mdl-cell mdl-cell--2-col"}]
        [:div {:class "mdl-shadow--2dp mdl-cell mdl-cell--8-col"}
          [:div {:class ""}
            [:h2 {:class ""} "Candidates"]]
          [:div {:class ""}
            [:ul {:class "mdl-list"}
              [:li {:class "mdl-list__item"}
                [:span {:class "mdl-list__item-primary-content"}
                  "Donald Trump"]
                [:span {:class "mdl-list__item-secondary-action"}
                  [:i {:class "material-icons mdl-list__item-icon"} "drag_handle"]]]
              [:li {:class "mdl-list__item"}
                [:span {:class "mdl-list__item-primary-content"}
                  "Ted Cruz"]
                [:span {:class "mdl-list__item-secondary-action"}
                  [:i {:class "material-icons mdl-list__item-icon"} "drag_handle"]]]
              [:li {:class "mdl-list__item"}
                [:span {:class "mdl-list__item-primary-content"}
                  "Marco Rubio"]
                [:span {:class "mdl-list__item-secondary-action"}
                  [:i {:class "material-icons mdl-list__item-icon"} "drag_handle"]]]
              [:li {:class "mdl-list__item"}
                [:span {:class "mdl-list__item-primary-content"}
                  "John Kasich"]
                [:span {:class "mdl-list__item-secondary-action"}
                  [:i {:class "material-icons mdl-list__item-icon"} "drag_handle"]]]]]]
        [:div {:class "mdl-cell--2-col"}]]]])

(defn mdl-init! [elem]
  (.upgradeElement js/componentHandler elem))

(defn mdl-init-mount [this-component]
  (mdl-init! (reagent/dom-node this-component)))

(def wrapped-body (vary-meta body-component (fn [meta-m] (merge-with juxt {:component-did-mount mdl-init-mount}))))

(reagent/render [wrapped-body] (.-body js/document))
