(defproject
  bughunt "0.1.0-SNAPSHOT"
  :description "Hunts for bugs on Launchpad"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211" :scope "provided"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.clojure/tools.logging "0.3.1"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-json "0.3.1"]
                 [hiccup "1.0.5"]
                 [reagent "0.5.0"]
                 [reagent-utils "0.1.4"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.3.3"]
                 [clj-http "1.1.0"]
                 [clj-time "0.9.0"]
                 [korma "0.4.0"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [org.xerial/sqlite-jdbc "3.8.7"]
                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]]

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js"]

  :plugins [[lein-cloverage "1.0.2"]
            [lein-ring "0.8.13"]
            [lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.2.9"]]

  :ring {:handler bughunt.server/app}

  :figwheel {:ring-handler bughunt.server/app
             :server-port  3449}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler     {:main          "bughunt.core"
                                            :output-to     "resources/public/js/app.js"
                                            :output-dir    "resources/public/js/out"
                                            :asset-path    "js/out"
                                            :optimizations :none
                                            :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns          bughunt.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.3.2"]
                                  [weasel "0.6.0"]
                                  [leiningen-core "2.5.1"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.10"]
                                  [pjstadig/humane-test-output "0.7.0"]]

                   :source-paths ["env/dev/clj"]
                   :plugins      [[lein-figwheel "0.2.9"]
                                  [lein-cljsbuild "1.0.5"]]

                   :figwheel     {:http-server-root "public"
                                  :server-port      3449
                                  :css-dirs         ["resources/public/css"]
                                  :ring-handler     bughunt.server/app}

                   :env          {:dev true}

                   :cljsbuild    {:builds {:app {:source-paths ["env/dev/cljs"]
                                                 :compiler     {:main       "bughunt.dev"
                                                                :source-map true}}}}}})
