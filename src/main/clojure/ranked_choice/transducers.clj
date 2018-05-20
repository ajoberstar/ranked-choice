(ns ranked-choice.transducers
  (:refer-clojure :exclude [peek reductions]))

(defn peek
  "Returns a transducer allowing visibility into input as it
  proceeds through the transducer stack. Takes a
  function f, which presumably has side-effects.

  For each input received by the returned transducer
  (f input) will be called before proceeding with the
  nested transducer."
  [f]
  (fn [xf]
    (completing
      (fn [result input]
        (f input)
        (xf result input)))))

(defn reductions
  "Returns a transducer that behaves similarly to clojure.core/reductions
  by applying the reducing function rf to the accumulated result and the
  new input. The result of callilng rf will be passed to the next transducer
  in the stack.

  If no init value is provided, this will behave is if you called
  (reductions rf (rf)). That is, (rf) will be executed eagerly before
  returning the transducer."
  ([rf]
   (reductions rf (rf)))
  ([rf init]
   (fn [xf]
     (let [acc (volatile! init)]
       (completing
         (fn [result input]
           (->> input (vswap! acc rf) (xf result))))))))
