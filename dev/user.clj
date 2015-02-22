 (ns user
   (:require [reloaded.repl :refer :all]
             [ranked-choice.core]))

 (set-init! #(ranked-choice.core/system {}))
