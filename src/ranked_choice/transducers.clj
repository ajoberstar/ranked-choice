(ns ranked-choice.transducers
  (:refer-clojure :exclude [peek reductions]))

(defn peek
  [f]
  (fn [xf]
    (completing
      (fn [result input]
        (f input)
        (xf result input)))))

(defn reductions
  ([rf]
    (reductions rf (rf)))
  ([rf init]
    (fn [xf]
      (let [acc (atom init)]
        (completing
          (fn [result input]
            (->> input (swap! acc rf) (xf result))))))))
