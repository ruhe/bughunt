(defproject bughunt "0.1.0-SNAPSHOT"
  :description "Hunts for bugs on Launchpad"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-http "1.1.0"]
                 [clj-time "0.9.0"]
                 [korma "0.4.0"]
                 [mysql/mysql-connector-java "5.1.6"]])
