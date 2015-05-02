(ns bughunt.web
  (:require [bughunt.constants :as const]
            [bughunt.helpers :as h]
            [clojure.string :as str]))

(defn date-filter-attr? [attr]
  (.startsWith (name attr) "date-"))

(defn assoc-date-filter
  "Associates given date predicate with predicates map.
  attr should be one of allowed date proprties of a bug
  (see const/BUG_DATE_FIELDS). pred should be a date
  string in the following format 'YYYY-MM-dd'.
  Example: (assoc-date-filter {} :date-created-from '2015-01-01')
           => {:date-created {:from <DateTime 2015-01-01>}}."
  [predicates attr pred]
  (let [chunks  (str/split (name attr) #"-")
        title   (->> (butlast chunks)
                     (str/join "-")
                     keyword)
        postfix (-> (last chunks) keyword)
        date (h/parse-date-str pred)]
    (if date
      (assoc-in predicates
                [title postfix]
                date)
      predicates)))

(defn add-predicate [predicates attr pred]
  (cond
    (date-filter-attr? attr)
    (assoc-date-filter predicates attr pred)

    :else (if (coll? pred)
            (assoc predicates attr pred)
            (assoc predicates attr [pred]))))

(defn reduce-params [predicates [attr pred]]
  (if pred
    (add-predicate predicates attr pred)
    predicates))

(defn parse-params [params]
  (reduce reduce-params
          {}
          (select-keys params const/SEARCH_FIELDS)))

(defn keyify-params [params]
  (zipmap (map keyword (keys params))
          (vals params)))
