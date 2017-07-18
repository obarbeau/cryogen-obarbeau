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

; based on cryogen-core "0.1.56"
; rewrite-hrefs will be called by the markdown plugin
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
                         (s/replace text #"(?!href=.?//)href=.?/|(?!src=.?//)src=.?/"
                                    #(str (subs % 0 (dec (count %)))
                                          blog-prefix "/")))
                       #"\|.*target.*=(.*)'" "' target='$1'")))

  (intern 'cryogen-core.compiler
          (with-meta 'add-prev-next
                     {:doc (str "Adds a :prev and :next key to the page/post"
                                " data containing the metadata of the "
                                "prev/next post/page if it exists")})
          (fn
            [pages]
            (map (fn [[next target prev]] ; before [prev target next]
                   (assoc target
                     :prev (if prev (dissoc prev :content) nil)
                     :next (if next (dissoc next :content) nil)))
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
