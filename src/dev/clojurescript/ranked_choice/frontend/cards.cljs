(ns ranked-choice.frontend.cards
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [devcards.core :as dc :refer-macros [defcard start-devcard-ui!]]
            [ranked-choice.frontend.main :as main]))

(start-devcard-ui!)

(defcard candidate
  (main/candidate {:name "Barack Obama"}))

(defcard candidate-list
  (main/candidate-list
    {:candidates
     [{:name "George Washington"}
      {:name "John Adams"}
      {:name "Thomas Jefferson"}]}))
