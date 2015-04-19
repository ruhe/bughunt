(ns bughunt.launchpad
  (:require [clj-time.format :as time-format]
            [clojure.core.memoize :as memo]
            [clojure.tools.logging :as log]
            [clj-http.client :as http]
            [bughunt.constants :as const])
  (:use [bughunt.helpers]))

(def PROJECT_URL "https://api.launchpad.net/1.0/%s")

(def ACTIVE_MILESTONES_URL
  "https://api.launchpad.net/1.0/%s/active_milestones")

(def lp-date-formatter
  (time-format/formatter "YYYY-MM-DD'T'HH:mm:ss.SSSSSSZ"))

;; Common functions

(defn- link->user-id [link]
  (let [x (last-part-of-url link)]
    (if x
      (subs x 1)
      nil)))

(defn- parse-lp-date [date-str]
  (try
    (time-format/parse lp-date-formatter date-str)
    (catch Exception e
      nil)))

;; Projects

(defn- get-active-milestones [project-name]
  (map #(select-keys % [:name :title])
       (:entries (http-get (format ACTIVE_MILESTONES_URL
                                   project-name)))))

(defn- get-project [name]
  (into
    (select-keys (http-get (format PROJECT_URL name))
                 [:title :name])
    {:active_milestones (get-active-milestones name)}))

;; Bugs

(defn- duplicate? [bug]
  (some? (:duplicate_of_link bug)))

(defn get-bug-internal [url]
  (let [raw (http-get url)]
    {:bug_id (:id raw)
     :title (:title raw)
     :tags (:tags raw)
     :is-duplicate (duplicate? raw)
     :owner (link->user-id (:owner_link raw))}))

(def get-bug (memoize-ttl get-bug-internal))

;; Search tasks

(defn- generate-search-parameters [start size]
  (http/generate-query-string
   {"ws.op" "searchTasks"
    "ws.size" size
    "memo" start
    "ws.start" start
    "omit_duplicates" "false"
    "status" const/BUG_STATUSES}))

(defn- get-lp-collection! [url callback]
  (let [resp (http-get url)
        entries (:entries resp)
        next-coll (:next_collection_link resp)]
    (do (callback entries)
        (when next-coll
          (get-lp-collection next-coll callback)))))

(defn search-tasks! [project-name callback]
  (let [url (str (format PROJECT_URL project-name) "?"
        (generate-search-parameters 0 100))]
    (get-lp-collection! url callback)))

(defn xform-task [raw]
  (into
   {:importance (:importance raw)
    :status     (:status raw)
    :target     (:bug_target_name raw)
    :milestone  (last-part-of-url (:milestone_link raw))
    :assignee   (link->user-id (:assignee_link raw))}
   [(get-bug (:bug_link raw))
    (map-selected parse-lp-date raw const/BUG_DATE_FIELDS)]))
