(ns bughunt.db-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [korma.core :as sql]
            [korma.db :as db]
            [bughunt.db :refer :all]))

;; Setup test in-memory sqlite database

(db/defdb testdb (db/sqlite3 {:db ""}))
(defonce EDN (slurp (io/resource "create_db.sql")))

(defn- exec-raw [q]
  (sql/exec-raw (db/get-connection testdb) q :keys))

(defn- drop-table []
  (exec-raw "DROP TABLE IF EXISTS bugs;"))

(use-fixtures :each
  (fn [f]
    (db/default-connection testdb)
    (drop-table)
    (exec-raw EDN)
    (f)
    (drop-table)))


;; Tests

(def pk-seq (atom 1))

(defn- create-bug []
  {:bug-id         (swap! pk-seq inc)
   :title          "the title"
   :owner          "ruhe"
   :importance     "Critical"
   :status         "Confirmed"
   :target         "foo"
   :milestone      "1.0"
   :assignee       "ruhe"
   :date-assigned  (t/now)
   :date-confirmed (t/now)})


(deftest test-insert-bug-task
  (do
    (insert-bug (create-bug))
    (insert-bug (create-bug))
    (is (= 2 (count (bug-report {:target "foo"}))))))

(deftest test-get-topn
  (do
    (doall (repeatedly 10 #(insert-bug (create-bug))))
    (is (= [{:cnt 10, :owner "ruhe"}]
           (topn 5 :owner)))))
