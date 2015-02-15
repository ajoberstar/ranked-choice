 (ns user
   (:require [com.stuartsierra.component :as component]
             [clojure.tools.namespace.repl :refer [refresh-all]]
             [ranked-choice.core :as core]))

 (def system nil)

 (defn- alter-system [f]
   (alter-var-root #'system f))

 (defn start []
   (alter-system component/start-system))

 (defn stop []
   (alter-system component/stop-system))

 (defn- start-fresh []
   (alter-system (fn [_] (core/system {})))
   (start)
   :ready)

 (defn reload []
   (stop)
   (refresh-all :after 'user/start-fresh))
