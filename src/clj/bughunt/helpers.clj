(ns bughunt.helpers
  (:require [clojure.string :as str]
            [clojure.core.memoize :as memo]
            [clojure.tools.logging :as log]
            [clj-http.client :as http]
            [clj-time.format :as time-format]))

(defn memoize-ttl
  "Returns memoized version of function. Default value for TTL
  is 60 seconds. TTL is configured in milliseconds."
  ([f] (memo/ttl f :ttl/threshold 60000))
  ([f ttl] (memo/ttl f :ttl/threshold ttl)))

(defn in [x coll]
  (some #{x} coll))

(defn parse-date-str [date-str]
  (try
    (time-format/parse (time-format/formatters :date)
                       date-str)
    (catch IllegalArgumentException _ nil)
    (catch ClassCastException _ nil)))

(defn map-selected
  "Selects entries from coll whose keys are in keyseq and applies f to values"
  [f coll keyseq]
  (let [x (select-keys coll keyseq)]
    (zipmap (keys x)
            (map f (vals x)))))

(defn transform-keys [f m]
  (zipmap (map (comp keyword f) (keys m)) (vals m)))

(defn underscorify-keys [m]
  (transform-keys #(str/replace (name %) #"-" "_") m))

(defn dashify-keys [m]
  (transform-keys #(str/replace (name %) #"_" "-") m))

;;--------------------- HTTP stuff-----------------------;;

(defn http-get [url]
  (log/debug "calling url: " url)
  (:body (http/get url {:as :json})))

(defn split-url
  "Splits given url in a vector of url parts"
  [url]
  (if url
    (filter #(not (empty? %))
            (clojure.string/split url #"/"))
    nil))

(defn last-part-of-url [url]
  (if url
    (last (split-url url))
    nil))
