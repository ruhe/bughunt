(ns bughunt.db
  (:require [clojure.set :as st]
            [clj-time.coerce :as tc]
            [korma.db :as db]
            [korma.core :as sql]
            [bughunt.constants :as const]))

;; Setup database connection

(db/defdb prod (db/mysql {:user     "demo"
                          :password "demo"
                          :db       "demo"}))


;; Define entities and tranformations on them

(defn generic-transform
  [f ent fields]
  (let [update-fn f
        ent ent
        fields (vec (st/intersection (set (keys ent)) (set fields)))]
    (reduce #(update-in %1 [%2] update-fn) ent fields)))

(defn tranform-dates [f ent]
  (generic-transform f ent const/BUG_DATE_FIELDS))

(sql/defentity
  bugs
  (sql/prepare #(tranform-dates tc/to-sql-time %))
  (sql/transform #(tranform-dates tc/from-sql-time %)))


;; Operations

(defn insert-bug [row]
  (do
    (sql/delete bugs (sql/where {:bug_id (:bug_id row)}))
    (sql/insert bugs
                (sql/values (dissoc row :tags :is-duplicate)))))


(defn report [filters]
  (sql/select bugs
              (sql/fields :bug_id :title :assignee :target :milestone
                      :importance :status)
              (sql/where filters)))
