(ns bughunt.server
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [bughunt.db :as db]
            [bughunt.filters :as f]
            [bughunt.web :as w]))

(defn get-bugs [query-params]
  (f/filter-by (w/parse-params (w/keyify-params query-params))
               (db/bug-report {})))

(defroutes site-routes
           (route/resources "/")
           (route/not-found "Not Found"))

(defroutes api-routes
           (context "/api" []
             (GET "/bugs"
                  {params :query-params}
               {:status 200
                :body   (get-bugs params)})
             (route/not-found "API not found")))

(defroutes api-and-site
           (wrap-json-response (handler/api api-routes))
           (handler/site site-routes))

(def app api-and-site)
