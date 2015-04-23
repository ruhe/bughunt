(ns bughunt.launchpad-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [bughunt.launchpad :refer :all]
            [bughunt.helpers :as h]))

(deftest test-user-id
  (is (= "ruhe"
         (link->user-id "https://launchpad.net/~ruhe")))
  (is (= nil (link->user-id nil))))

(deftest test-parse-lp-date
  (let [dt (parse-lp-date "2014-02-03T18:24:54.998594+00:00")]
    (is (= 2014 (t/year dt)))
    (is (= 2 (t/month dt)))
    (is (= 3 (t/day dt)))
    (is (= 18 (t/hour dt)))
    (is (= 24 (t/minute dt)))
    (is (= 54 (t/second dt))))
  (testing "That parse-lp-date returns nil instead of throwing exception"
    (is (nil? (parse-lp-date "my birthday")))))

;; Projects

(def raw-milestones
  {:entries [
             {:name "1.0.0"
              :release_link nil
              :title "Fancy Release 1.0"
              :is_active true
              :web_link "https://launchpad.net/foobar/+milestone/1.0"
              :summary ""
              :official_bug_tags ["a", "b", "c"]}
             {:name "2.1.0"
              :release_link nil
              :title "Fancier Release 2.1.0"
              :is_active true
              :web_link "https://launchpad.net/foobar/+milestone/2.1.0"
              :summary "Some summary"
              :official_bug_tags ["a", "b", "c"]}]})


(deftest test-tranform-milestone-resp
  (is (= '({:name "1.0.0" :title "Fancy Release 1.0"}
           {:name "2.1.0" :title "Fancier Release 2.1.0"})
         (transform-active-milestones-resp raw-milestones)))
  (is (= '() (transform-active-milestones-resp {:entries []}))))

(deftest test-transform-project-resp
  (is (= {:name "foo" :title "Enterprise Foo"}
         (transform-project-resp {
                                  :name "foo"
                                  :title "Enterprise Foo"
                                  :tags []
                                  :link "http://example.foo/foo"}))))

(deftest test-get-project
  (with-redefs [get-project-info
                (fn [_] {:name "foo"
                         :title "Enterprise Foo"})
                get-active-milestones
                (fn [_] '({:name "1.0" :title "Release 1.0"}))]
    (is (= {:name "foo"
            :title "Enterprise Foo"
            :active-milestones [{:name "1.0" :title "Release 1.0"}]}
           (get-project "foo")))))


;; Bugs

(def raw-bug
  {:id 1
   :title "Title"
   :tags ["tag1" "tag2"]
   :owner_link "https://launchpad.net/~ruhe"
   :duplicate_of_link nil
   :field1 :ignore
   :field2 "ignored"})

(deftest test-transform-bug
  (is (= {:bug_id 1
          :title "Title"
          :tags ["tag1" "tag2"]
          :owner "ruhe"
          :is-duplicate false}
         (transform-bug raw-bug))))

(def raw-bug-task
  {:importance "High"
   :status "Confirmed"
   :bug_target_name "foo"
   :milestone_link "https://launchpad.net/foo/~1.0"
   :assignee_link "https://launchpad.net/~ruhe"
   :date_created "1993-11-19T22:58:26.755169+00:00"
   :date_assigned "2015-02-11T17:29:00.064664+00:00"
   :date_confirmed "2014-11-28T16:36:47.458644+00:00"})

(deftest test-contruct-bug
  (with-redefs [get-bug-info (fn [_] {:bug_id 1
                                      :title "Something"
                                      :tags ["a" "b" "c"]
                                      :owner "ruhe"
                                      :is-duplicate? false})]
    (let [c (construct-bug raw-bug-task)]
      (is (every? c [:importance
                     :status
                     :milestone
                     :target
                     :assignee
                     :date_assigned
                     :date_created
                     :date_confirmed])))))

;; Search bugs

(defn- fake-http-get [_]
  {:next_collection_link nil
   :entries (repeat 10 raw-bug-task)})

;; TODO(ruhe): test condition where next_collection_link is not empty
(deftest test-search-bug-tasks
  (with-redefs [h/http-get fake-http-get]
    (let [cnt (atom 0)
          callback (fn [_] (swap! cnt inc))
          _ (search-bug-tasks "fake_url" callback)]
      (is (= 1 @cnt)))))
