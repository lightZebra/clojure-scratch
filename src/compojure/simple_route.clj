(ns compojure.simple-route
  (:require [org.httpkit.server :refer :all]
            [compojure.route :refer :all]
            [compojure.core :refer :all]
            [ring.util.response :as ring-response]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint])
  (:import (java.io ByteArrayInputStream InputStream)))

(defonce server (atom nil))

(defn long-byte-stream [_]
  {:status  200
   :headers {"Content-Type"       "text/csv"
             "Content-Disposition" "attachment; filename=\"long-stream.txt\""}
   :body    (proxy [InputStream] []
              (read
                ([] (rand-int 1000))
                ([^bytes bytes] 1)
                ([^bytes bytes off len] 1)))})
   ;:body    (System/in)})

(defn prettifyRequest [req]
  (str "<pre>" (with-out-str (pprint/pprint req)) "<pre>"))

(defn file [_]
  (ring-response/file-response "README.md"))

(defn stream [_]
  {:status  200
   :headers {"Content-Type"       "application/octet-stream"
             "Content-Disposition" "attachment; filename=\"temp.txt\""}
   :body    (io/input-stream (ByteArrayInputStream. (.getBytes "text")))})

(defroutes
  handler
  (GET "/" [] "hello compojure")
  (GET "/get/:name" [name] (str "Hello " name))
  (GET "/request" [] prettifyRequest)
  (GET "/file" [] file)
  (GET "/stream" [] stream)
  (GET "/long-stream" [] long-byte-stream)
  (not-found "<p>Page not found.</p>"))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and http://http-kit.org/migration.html#reload
  (reset! server (run-server #'handler {:port 8081}))
  (println "start server"))

(-main)