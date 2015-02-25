(defproject ranked-choice "0.1.0-SNAPSHOT"
  :description "Web app for ranked choice voting."
  :url "https://github.com/ajoberstar/ranked-choice"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :min-lein-version "2.5.0"
  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/data.json "0.2.5"]

                 ;; component
                 [com.stuartsierra/component "0.2.2"]

                 ;; http
                 [ring/ring-core "1.3.2"]
                 [http-kit "2.1.19"]
                 [compojure "1.3.2"]
                 [enlive "1.1.5"]]
  :java-agents [[com.newrelic.agent.java/newrelic-agent "3.13.0"]]
  :main ^:skip-aot ranked-choice.core
  :target-path "target/%s"
  :uberjar-name "ranked-choice.jar"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[reloaded.repl "0.1.0"]
                                  [org.clojure/tools.namespace "0.2.9"]]
                   :source-paths ["dev"]}})
