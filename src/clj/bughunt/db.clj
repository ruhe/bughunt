(ns bughunt.db
  (:require [clojure.set :as set]
            [clojure.tools.logging :as log]
            [clj-time.coerce :as tc]
            [korma.db :as db]
            [korma.core :as sql]
            [bughunt.constants :as const]
            [bughunt.helpers :as h]))

;; Setup database connection

(db/defdb prod (db/mysql {:user     "demo"
                          :password "demo"
                          :db       "demo"}))


;; Define entities and tranformations on them

(defn- generic-transform
  [f ent fields]
  (let [update-fn f
        ent ent
        fields (vec (set/intersection (set (keys ent)) (set fields)))]
    (reduce #(update-in %1 [%2] update-fn) ent fields)))

(defn- tranform-dates [f ent]
  (generic-transform f ent const/BUG_DATE_FIELDS))

(sql/defentity
  bugs
  (sql/table :BUGS)
  (sql/prepare #(h/underscorify-keys %))
  (sql/prepare #(tranform-dates tc/to-sql-time %))
  ;;(sql/transform #(h/dashify-keys %))
  (sql/transform #(tranform-dates tc/from-sql-time %)))

;; Delete Operations

(defn delete-bug [bug-id]
  (sql/delete bugs (sql/where {:bug_id bug-id})))

;; Insert Operations

(defn insert-bug [row]
  (log/debug "Inserting bug task " row)
  (do
    (delete-bug (:bug-id row))
    (sql/insert bugs
                (sql/values (dissoc row :tags :is-duplicate)))))

;; Filtering functions (translates to SQL's WHERE clause)

(defn- translate-filter [base f]
  (let [k (first f)
        v (second f)]
    (cond
      (coll? v) (-> base (sql/where {k [in v]}))
      :else (-> base (sql/where {k v})))))

(defn- translate-filters [base filters]
  (reduce translate-filter base filters))

;; Reporting operations

(def ^:private report-fields
  [:assignee
   :bug_id
   :importance
   :milestone
   :status
   :target
   :title])

(def ^:private base-report
  (-> (sql/select* bugs)
      (#(apply sql/fields % report-fields))))

(defn- report-internal [filters]
  (sql/exec (translate-filters base-report filters)))

(def bug-report (h/memoize-ttl report-internal))

;; "TOP N by something" operations

(defn- base-topn [n field]
  (-> (sql/select* bugs)
      (sql/fields field)
      (sql/aggregate (count field) :cnt)
      (sql/group field)
      (sql/order :cnt :DESC)
      (sql/limit n)))

(defn- topn-internal
  ([n field] (topn-internal n field {}))
  ([n field filters]
   (sql/exec (translate-filters (base-topn n field) filters))))

(def topn (h/memoize-ttl topn-internal))
