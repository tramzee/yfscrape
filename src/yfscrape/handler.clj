(ns yfscrape.handler
  (:require [compojure.core :refer :all]
            [yfscrape.yahoo :refer [component-info market-cap]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.core :refer [html h]]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(def idx-dax "GDAXI")
(def idx-cac40 "FCHI")

(defn component-template [name idx-sym desc]
  (let [component-data (component-info idx-sym)
        total-cap (reduce + (map market-cap component-data))]
    (html5
     [:head
      [:title desc]]
     [:body
      [:div.datatable
       [:table
        [:tr [:th "Name"] [:th "Symbol"] [:th "Market Cap"] [:th "Percent"]]
        [:tr [:td name] [:td idx-sym] [:td (format "%.2f" total-cap)] [:td 100]]
        (map (fn [d]
               [:tr
                [:td (:title d)]
                [:td (:sym d)]
                [:td (or (:market-cap d) "N/A")]
                [:td (if (:market-cap d)
                       (format "%.2f" (/ (* 100 (market-cap d)) total-cap))
                       "N/A")]])
             component-data)]]])))


(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/dax" [] (component-template "DAX Index" idx-dax "DAX Index Components"))
  (GET "/cac40" [] (component-template "CAC 40 Index" idx-cac40 "CAC 40 Index Components"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
