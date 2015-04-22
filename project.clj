(defproject bughunt "0.1.0-SNAPSHOT"
  :description "Hunts for bugs on Launchpad"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-http "1.1.0"]
                 [clj-time "0.9.0"]
                 [korma "0.4.0"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [org.xerial/sqlite-jdbc "3.8.7"]
                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]]
  :plugins [[lein-cloverage "1.0.2"]])
