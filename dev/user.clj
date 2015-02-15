 (ns user
   (:require [com.stuartsierra.component :as component]
             [clojure.tools.namespace.repl :refer [refresh-all]]
             [ranked-choice.core :as core]))

 (def system nil)

 (defn init []
   (alter-var-root #'system (fn [sys]
                              (core/system {}))))

 (defn start []
   (alter-var-root #'system component/start-system))

 (defn stop []
   (alter-var-root #'system component/stop-system))

 (defn go []
   (init)
   (start)
   :ready)

 (defn reset []
   (stop)
   (refresh-all :after 'user/go))
