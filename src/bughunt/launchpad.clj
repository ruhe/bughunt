(ns bughunt.launchpad
  (:require [clojure.tools.logging :as log]
            [clj-http.client :as http]))

(def PROJECT_URL "https://api.launchpad.net/1.0/%s")
(def ACTIVE_MILESTONES_URL "https://api.launchpad.net/1.0/%s/active_milestones")

(def BUG_URL "https://api.launchpad.net/1.0/bugs/%d")

(defn- json-get [url]
  (log/debug "Calling url:" url)
  (:body (http/get url {:as :json})))

(defn- json-get-keys [url keys]
  (select-keys (json-get url) keys))

(defn- get-active-milestones [project-name]
  (map #(select-keys % [:name :title])
       (:entries (json-get (format ACTIVE_MILESTONES_URL
                                   project-name)))))

(defn- get-project [name]
  (into
   (json-get-keys (format PROJECT_URL name) [:title :name])
   {:active_milestones (get-active-milestones name)}))
