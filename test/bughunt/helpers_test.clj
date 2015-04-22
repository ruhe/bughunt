(ns bughunt.helpers-test
  (:require [clojure.test :refer :all]
            [bughunt.helpers :refer :all]))

;; Not sure if I need to test what's already tested
(deftest test-memoize-ttl
  (let [cnt (atom 0)
        memo (memoize-ttl (fn [] (swap! cnt inc)))
        _ (doall (repeatedly 10 memo))]
    (is (= 1 @cnt))))

(deftest test-map-selected
  (is (= {:a 2 :d 5}
         (map-selected inc
                       {:a 1 :b 2 :c 3 :d 4}
                       [:a :d])))
  (is (= {}
         (map-selected identity
                       {:a 1 :b 2}
                       [])))
  (is (= {}
         (map-selected identity {} []))))

(deftest test-split-url
  (is (= ["https:" "example.com" "example"]
         (split-url "https://example.com/example")))
  (is (nil? (split-url nil))))

(deftest test-last-part-of-url
  (is (= "second" (last-part-of-url "https://example.com/second")))
  (is (= nil (last-part-of-url nil))))
