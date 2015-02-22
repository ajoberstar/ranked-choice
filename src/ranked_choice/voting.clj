(ns ranked-choice.voting)

(defprotocol VotingSystem
  (count-votes [vsys votes]))

(defn- format-results
  [results]
  (->> results
       (map (fn [[candidate votes]]
              (into [candidate] votes)))
       (sort-by (comp #(into [] %) reverse))
       reverse
       (into [])))

(defn results
  ([vsys]
    (comp format-results
          (partial count-votes vsys)))
  ([vsys votes]
    (-> vsys results votes)))
