(ns ranked-choice.frontend.cards
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [devcards.core :as dc :refer-macros [defcard start-devcard-ui!]]))

(start-devcard-ui!)

(defcard
  "## This is markdown!

 It is the **greatest** `markup` language!.
 ")

(defui Hello
  Object
  (render [this]
          (dom/p nil (str "Hello, " (-> this om/props :name) "!"))))

(def hello (om/factory Hello))

(defcard simple-hello
  (hello {:name "Andy"}))
