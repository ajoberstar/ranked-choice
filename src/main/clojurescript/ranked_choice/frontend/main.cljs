(ns ranked-choice.frontend.main
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [devcards.core :as dc :refer-macros [defcard defcard-om-next defcard-doc start-devcard-ui!]]
            [ranked-choice.frontend.cards]))

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
