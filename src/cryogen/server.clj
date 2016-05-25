(ns cryogen.server
  (:require [clojure.string :as s]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [redirect resource-response]]
            [ring.util.codec :refer [url-decode]]
            [cryogen-core.watcher :refer [start-watcher!]]
            [cryogen-core.plugins :refer [load-plugins]]
            [cryogen-core.compiler :refer [compile-assets-timed read-config]]
            [cryogen-core.io :refer [path]]))

(defn interns []
  (intern 'cryogen-core.markup
          (with-meta 'rewrite-hrefs
                     {:doc (str "Injects the blog prefix in front of any "
                                "local links ex. <img src='/img/cryogen.png'/> "
                                "becomes <img src='/blog/img/cryogen.png'/>"
                                "Also add target attribute if needed")})
          (fn [blog-prefix text]
            (s/replace (if (s/blank? blog-prefix)
                         text
                         (s/replace text #"href=.?/|src=.?/"
                                    #(str (subs % 0 (dec (count %)))
                                          blog-prefix "/")))
                       #"\|.*target.*=(.*)'" "' target='$1'")))

  (intern 'cryogen-core.compiler
          (with-meta 'add-prev-next
                     {:doc (str "Adds a :prev and :next key to the page/post"
                                " data containing the title and uri of the "
                                "prev/next post/page if it exists")})
          (fn
            [pages]
            (map (fn [; oba [prev target next]
                      [next target prev]]
                   (assoc target
                     :prev (if prev (select-keys prev [:title :uri]) nil)
                     :next (if next (select-keys next [:title :uri]) nil)))
                 (partition 3 1 (flatten [nil pages nil]))))))

(defn init []
  (interns)
  (load-plugins)
  (compile-assets-timed)
  (let [ignored-files (-> (read-config) :ignored-files)]
    (start-watcher! "resources/templates" ignored-files compile-assets-timed)))

(defn wrap-subdirectories
  [handler]
  (fn [request]
    (let [req-uri (.substring (url-decode (:uri request)) 1)
          res-path (path req-uri (when (:clean-urls? (read-config)) "index.html"))]
      (or (resource-response res-path {:root "public"})
          (handler request)))))

(defroutes routes
           (GET "/" [] (redirect (let [config (read-config)]
                                   (path (:blog-prefix config) "/"
                                         (when-not (:clean-urls? config) "index.html")))))
           (route/resources "/")
           (route/not-found "Page not found"))

(def handler (wrap-subdirectories routes))
