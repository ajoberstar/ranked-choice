(ns ranked-choice.backend.dev
  (:require [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :refer [reset set-init start stop system]]
            [gradle-clojure.tools.figwheel :as fw]))

(defn new-system [_]
  (component/system-map))

(set-init new-system)

(swap! gradle-clojure.tools.clojure-nrepl/context assoc-in [:cljs-builds "dev" :figwheel :devcards] true)
