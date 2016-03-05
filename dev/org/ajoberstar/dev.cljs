(ns org.ajoberstar.dev
  (:require [figwheel.client :as fw]
            [org.ajoberstar.ranked-choice]))

(fw/start {:on-jsload (fn [] (print "reloaded"))})
