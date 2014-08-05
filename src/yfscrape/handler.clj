(ns yfscrape.handler
  (:require [compojure.core :refer :all]
            [yfscrape.dax :as dax]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.core :refer [html h]]
            [compojure.handler :as handler]
            [compojure.route :as route]))


(defn show-dax []
  (let [dax-sym dax/dax-idx
        component-data (dax/components)
        total-cap (reduce + (map :market-cap component-data))]
   (html5
    [:head
     [:title "DAX Index Components"]]
    [:body
     [:div.datatable
      [:table
       [:tr
        [:th "Name"]
        [:th "Symbol"]
        [:th "Market Cap"]
        [:th "Percent"]]
       [:tr
        [:td "DAX Index"]
        [:td dax-sym]
        [:td total-cap]
        [:td 100]]
       (map (fn [d]
              [:tr
               [:td (:title d)]
               [:td (:sym d)]
               [:td (:market-cap d)]
               [:td (format "%.2f" (/ (* 100 (:market-cap d)) total-cap))]]) component-data)
       ]]])))


(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/dax" [] (show-dax))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
