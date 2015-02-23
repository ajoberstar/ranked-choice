(ns ranked-choice.voting)

(defprotocol VotingSystem
  "Protocol specifying how to
  determine a winner of a poll."
  (count-ballots [vsys candidates votes]))

(defn- format-results
  [results]
  (->> results
       (map (fn [[candidate votes]]
              (into [candidate] votes)))
       (sort-by (comp #(into [] %) reverse))
       reverse
       (into [])))

(defn results
  "Calculates the results of a series of votes using
  the given VotingSystem vsys. If the votes are left
  off, will return a function that takes votes.

  Votes must be in the format of a vector with exactly
  one occurrence of each candidate in order of the
  voter's preference.

  Results will be formatted into a vector of vectors,
  where the inner vector consists of the candidate's
  name followed by their total votes in each round of
  counting. The outer vector will be sorted by the
  ranking of the candidates (using the highest number
  of votes in the last round first, and using previous
  rounds as tie-breakers, as needed).

  Example Results:

  [[Susy 2 3 4]
   [Billy 2 2 3]
   [Johnny 2 2 0]
   [Lucy 1 0 0]]"
  ([vsys]
    (comp format-results
          (partial count-ballots vsys)))
  ([vsys votes]
    (-> vsys results votes)))
