(defproject ranked-choice "0.2.0-SNAPSHOT"
  :description "Web app for ranked choice voting."
  :url "https://github.com/ajoberstar/ranked-choice"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :min-lein-version "2.5.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/data.json "0.2.6"]

                 ;; component
                 [com.stuartsierra/component "0.3.1"]

                 ;; http
                 [ring/ring-core "1.4.0"]
                 [http-kit "2.1.19"]
                 [compojure "1.4.0"]
                 [enlive "1.1.6"]]
  :main ^:skip-aot ranked-choice.core
  :target-path "target/%s"
  :uberjar-name "ranked-choice.jar"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[reloaded.repl "0.2.1"]
                                  [org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["dev"]}})
