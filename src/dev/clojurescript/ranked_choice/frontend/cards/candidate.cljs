(ns ranked-choice.frontend.cards.candidate
  (:require [devcards.core :as dc :refer-macros [defcard]]
            [ranked-choice.frontend.main :as main]))

(defcard candidate
  (main/candidate {:name "Barack Obama"}))

(defcard candidate-list
  (main/candidate-list
    {:candidates
     [{:name "George Washington"}
      {:name "John Adams"}
      {:name "Thomas Jefferson"}
      {:name "Abraham Lincoln"}]}))
