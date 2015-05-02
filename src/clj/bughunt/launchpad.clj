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

;; Common functions

(defn link->user-id [link]
  (let [x (last-part-of-url link)]
    (if x
      (subs x 1)
      nil)))

(def ^:private lp-date-formatter
  (time-format/formatter "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"))

(defn parse-lp-date [date-str]
  (try
    (time-format/parse lp-date-formatter date-str)
    (catch Exception _
      nil)))

;; Projects

(defn transform-active-milestones-resp [resp]
  (map #(select-keys % [:name :title])
       (:entries resp)))

(defn get-active-milestones [project-name]
  (-> (format ACTIVE_MILESTONES_URL project-name)
      (http-get)
      (transform-active-milestones-resp)))

(defn transform-project-resp [resp]
  (select-keys resp [:title :name]))

(defn get-project-info [project-name]
  (-> (format PROJECT_URL project-name)
      (http-get)
      (transform-project-resp)))

(defn get-project [project-name]
  (into
    (get-project-info project-name)
    {:active-milestones (get-active-milestones project-name)}))

;; Bugs

(defn- duplicate-bug? [bug]
  (some? (:duplicate_of_link bug)))

(defn transform-bug [raw]
  {:bug_id        (:id raw)
   :title         (:title raw)
   :tags          (:tags raw)
   :owner         (link->user-id (:owner_link raw))
   :is-duplicate (duplicate-bug? raw)})

(defn- get-bug-internal [url]
  (transform-bug (http-get url)))

(def get-bug-info (memoize-ttl get-bug-internal))

(defn transform-bug-task [raw]
  {:importance (:importance raw)
   :status     (:status raw)
   :target     (:bug_target_name raw)
   :milestone  (last-part-of-url (:milestone_link raw))
   :assignee   (link->user-id (:assignee_link raw))})

(defn construct-bug [raw-bug-task]
  (into
   (transform-bug-task raw-bug-task)
   [(get-bug-info (:bug_link raw-bug-task))
    (map-selected parse-lp-date raw-bug-task const/BUG_DATE_FIELDS)]))

;; Search tasks

(defn- generate-search-parameters [start size]
  (http/generate-query-string
   {"ws.op" "searchTasks"
    "ws.size" size
    "memo" start
    "ws.start" start
    "omit_duplicates" "false"
    "status" const/BUG_STATUSES}))

(defn- get-lp-collection [url callback]
  (let [resp (http-get url)
        entries (:entries resp)
        next-coll (:next_collection_link resp)]
    (do (callback entries)
        (when next-coll
          (get-lp-collection next-coll callback)))))

(defn search-bug-tasks [project-name callback]
  (let [url (str (format PROJECT_URL project-name) "?"
                 (generate-search-parameters 0 100))]
    (get-lp-collection url callback)))
