(defproject ranked-choice "0.1.0-SNAPSHOT"
  :description "Web app for ranked choice voting."
  :url "https://github.com/ajoberstar/ranked-choice"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot ranked-choice.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
