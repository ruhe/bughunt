(ns bughunt.core
  (:require [bughunt.launchpad :as lp]
            [bughunt.db :as db]
            [clojure.core.async :as async]))


(def in-chan (async/chan))
(def out-chan (async/chan))

(defn start-async-consumers
  "Start num-consumers threads that will consume work
  from the in-chan and put it into the out-chan."
  [num-consumers]
  (dotimes [_ num-consumers]
    (async/thread
      (while true
        (let [raw-task (async/<!! in-chan)
              data (lp/xform-task raw-task)]
          (when-not (:is_duplicate data)
            (async/>!! out-chan data)))))))

(defn start-async-aggregator
  "Ineffective consumer. Inserts values one by one
  instead of aggregating them in batches."
  []
  (async/thread
    (while true
      (let [data (async/<!! out-chan)]
        (db/insert-bug data)))))

(defn process-bugs [project]
  (do
    (start-async-consumers 8)
    (start-async-aggregator)
    (lp/search-tasks! project
                      #(doseq [task %]
                         (async/>!! in-chan task)))))
