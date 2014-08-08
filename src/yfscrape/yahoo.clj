(ns yfscrape.yahoo
  (:require [net.cgrand.enlive-html :as html]))


(def ^:dynamic *base-url* "http://finance.yahoo.com")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn page [path]
  (fetch-url (str *base-url* path)))

(defn component-path [idx] (format "/q/cp?s=%%5E%s+Components" (clojure.string/upper-case idx)))

(defn component-page [idx] (page (component-path idx)))

(defn component-syms [idx] (html/select (component-page idx) [:td.yfnc_tabledata1 :a html/text-node]))

(defn str->mc [s] (and s (read-string (clojure.string/replace s #"[^0-9\\.]" ""))))

(defn sym-path [sym] (format "/q?s=%s" (clojure.string/upper-case sym)))

(defn sym-page [sym]
  (page (sym-path sym)))

(defn market-cap-page [p]
  (-> (html/select p [[:span (html/attr-starts :id "yfs_j10_")]])
      first
      :content
      first
      str->mc))

(defn prev-close [p]
  (-> (html/select p [:#table1 [:tr (html/nth-child 1)] :td html/text-node])
      first
      read-string))

(defn page-title [p]
  (first (html/select p [:.title :h2 html/text-node])))

(defn page-info [p]
  {:title (page-title p)
   :prev-close (prev-close p)
   :market-cap (market-cap-page p)})

(defn sym-info [sym]
  (assoc (page-info (sym-page sym)) :sym (clojure.string/upper-case sym)))

(defn market-cap [si]
  (or (:market-cap si) 0))

(defn component-info [idx]
  (map sym-info (component-syms idx)))
