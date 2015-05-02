(ns bughunt.filters
  (:require [clj-time.core :as t]
            [bughunt.helpers :as h]))

(defn- match-dates [value pred]
  (cond
    (contains? pred :after)
    (or (t/after? value (:after pred))
        (= value (:after pred)))

    (contains? pred :before)
    (or (t/before? value (:before pred))
        (= value (:before pred)))

    :else true))

(defn- create-filter [[attr pred]]
  (cond
    (h/in (name attr)
          ["date-created" "date-triaged"
           "date-fix-committed" "date-fix-released"])
    #(match-dates (get % attr) pred)

    (coll? pred)
    #(h/in (get % attr) pred)

    :else #(= (get % attr) pred)))

(defn- compile-predicates [predicates]
  (apply every-pred
         (map #(create-filter %) predicates)))

(defn filter-by [predicates bugs]
  (if (empty? predicates)
    bugs
    (filter (compile-predicates predicates)
            bugs)))
