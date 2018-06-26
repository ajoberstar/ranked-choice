(ns ranked-choice.frontend.main
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Candidate
  static om/Ident
  (ident [this {:keys [name]}]
    [:candidate/by-name name])
  Object
  (render [this]
    (let [{:keys [name]} (om/props this)]
      (dom/li nil name))))

(def candidate (om/factory Candidate {:keyfn :name}))

(defui CandidateList
  Object
  (render [this]
    (let [{:keys [candidates]} (om/props this)]
      (dom/ul nil (map candidate candidates)))))

(def candidate-list (om/factory CandidateList))
