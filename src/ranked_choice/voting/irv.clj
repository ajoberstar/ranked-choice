(ns ranked-choice.voting.irv
  (:require [ranked-choice.voting :as voting]))

(defn- tabulate
  [votes]
  (let [candidates (first votes)
        base-results (zipmap candidates (repeat 0))]
    (->> votes
         (map first)
         frequencies
         (into base-results))))

(defn- remove-candidate
  [votes candidate]
  (map (fn [vote]
         (remove #(= % candidate) vote))
       votes))

(defn- empty-results
  [candidates]
  (zipmap candidates (repeat [])))

(defrecord InstantRunoff []
  voting/VotingSystem
  (count-ballots [_ _ votes]
    (let [candidates (first votes)
          votes-to-win (/ (count votes) 2)
          base-results (zipmap candidates (repeat 0))]
      (loop [remaining-votes votes
             prev-results (empty-results candidates)]
        (let [round-results (tabulate remaining-votes)
              new-results (->> round-results
                               (into base-results)
                               (merge-with conj prev-results))
              most-votes (reduce max (vals round-results))
              last-place (first (apply min-key last round-results))]
          (if (> most-votes votes-to-win)
            new-results
            (recur (remove-candidate remaining-votes last-place) new-results)))))))
