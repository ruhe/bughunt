(ns bughunt.helpers
  (:require [clojure.core.memoize :as memo]
            [clj-http.client :as http]
            [clojure.tools.logging :as log]))

(defn memoize-ttl
  "Returns memoized version of function. Default value for TTL
  is 60 seconds. TTL is configured in milliseconds."
  ([f] (memo/ttl f :ttl/threshold 60000))
  ([f ttl] (memo/ttl f :ttl/threshold ttl)))

(defn map-selected
  "Selects entries from coll whose keys are in keyseq and applies f to values"
  [f coll keyseq]
  (let [x (select-keys coll keyseq)]
    (zipmap (keys x)
            (map f (vals x)))))

;;--------------------- HTTP stuff-----------------------;;

(defn http-get [url]
  (log/debug "calling url: " url)
  (:body (http/get url {:as :json})))

(defn split-url
  "Splits given url in a vector of url parts"
  [url]
  (clojure.string/split url #"/"))

(defn last-part-of-url [url]
  (if url
    (last (split-url url))
    nil))
