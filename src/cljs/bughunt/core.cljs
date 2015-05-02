(ns ^:figwheel-always bughunt.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]])
  (:import goog.History))

(defonce state
         (atom {:bugs []}))

(defn update-dom [bugs]
  (swap! state assoc :bugs bugs))

(defn get-bugs [search-opts-cursor]
  (let [search-opts @search-opts-cursor]
    (GET "/api/bugs" {:handler update-dom
                      :params  search-opts})))

(defn input [val placeholder]
  [:div.form-group
   [:input {:class       "form-control"
            :placeholder placeholder
            :value       @val
            :on-change   #(reset! val (.-target.value %))}]])

(defn button [title handler]
  [:button {:type     "submit"
            :class    "btn btn-default"
            :on-click #(do
                        (.preventDefault %)
                        (handler))} title])

(defn panel [title content]
  [:div {:class "panel panel-default"}
   [:div {:class "panel-heading"}
    [:h4 {:class "panel-title"} title]]
   [:div
    {:class "panel-body"}
    content]])

(defn bug-table []
  (let [bugs (:bugs @state)]
    (if (> (count bugs) 0)
      [:table {:class "table"}
       [:thead
        [:tr
         [:th "ID"]
         [:th "Title"]
         [:th "Status"]
         [:th "Importance"]
         [:th "Assignee"]]]
       [:tbody (for [bug bugs]
                 ^{:key (get bug "bug_id")}
                 [:tr
                  [:td (get bug "bug_id")]
                  [:td {:class "description"} (get bug "title")]
                  [:td (get bug "status")]
                  [:td (get bug "importance")]
                  [:td (get bug "assignee")]])]]
      [:h4 "Nothing to display"])))

(defn search-page []
  (let [local-state
        (atom {:bugs           []
               :search-options {:status     ""
                                :importance ""
                                :assignee   ""}})]
    [:div
     [panel "Configure search parameters"
      [:form {:class "form-inline"}
       [input (reagent/cursor local-state [:search-options :status]) "Status"]
       [input (reagent/cursor local-state [:search-options :importance]) "Importance"]
       [input (reagent/cursor local-state [:search-options :assignee]) "Assignee"]
       [button "Search" #(get-bugs (reagent/cursor local-state [:search-options]))]]]
     [bug-table]]))

(defn mount-root []
  (reagent/render [search-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
