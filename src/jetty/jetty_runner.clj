(ns jetty.jetty-runner
  (:require
    [compojure.core :refer [defroutes routes GET]]
    [compojure.route :as route]
    [ring.adapter.jetty :as jetty]
    [clojure.core.async :refer [go chan >! <!] :as a]
    [ring.middleware.reload :refer [wrap-reload]]
    [clj-http.client :as client]
    [ring.core.protocols :as ring-protocols]
    [clojure.java.io :as io])
  (:import (java.io SequenceInputStream ByteArrayInputStream InputStream OutputStream)
           (clojure.lang IPersistentVector)))

(def counter (atom 0))

(defn hello-world []
  (ring.util.response/response
    (ring.util.io/piped-input-stream
      (fn [output-stream]
        (spit output-stream "zxcasd")))))

(extend-protocol ring-protocols/StreamableResponseBody
  IPersistentVector
  (write-body-to-stream [[body] _ ^OutputStream output-stream]
    (do
      (println "extend-protocol")
      (io/copy body output-stream))))

(defn generate-string [len]
  (apply str (repeatedly len #(char (+ (rand 26) 65)))))


(defroutes
   handler
   (GET "/ping" [] (str "pong " (swap! counter inc)))
   (GET "/stream" [] (hello-world))
   (GET "/t/:appId" [app_id :as req]
     (println req)
     req)
   (GET "/huge-stream-with-error" [:as req]
     {:status  200
      :headers {"Content-Type"        "text/plain; charset=utf-8"
                "Content-Disposition" "attachment; filename=myFile.csv"}
      :body
               (let [stream (ByteArrayInputStream. (.getBytes (generate-string 1000000) "UTF-8"))
                     counter (atom 0)
                     process-read (fn [f]
                                    (println "asd" @counter)
                                    (Thread/sleep 1000)
                                    (if (zero? (mod (swap! counter inc) 100))
                                      (throw (OutOfMemoryError. "test"))
                                      (f)))]
                 [(proxy [InputStream] []
                    (read
                      ([] (process-read #(.read stream)))
                      ([^bytes bytes] (process-read #(.read stream bytes)))
                      ([^bytes bytes off len] (process-read #(.read stream bytes off len)))
                      ))])
      #_(SequenceInputStream.
          (ByteArrayInputStream. (.getBytes "asd-1" "UTF-8"))
          (proxy [InputStream] []
            (read [_] (throw (OutOfMemoryError. "test")))))})
   (GET "/sleep" [:as req]
     (println "before sleep")
     (Thread/sleep 1000000)
     (println "after sleep"))
   (GET "/page" []
     {:status 200
      ;:headers {
      ;          "Access-Control-Allow-Origin"  "*"
      ;          "Access-Control-Allow-Methods" "*"
      ;          "Access-Control-Allow-Headers" "*"
      ;          }
      :body   "<!DOCTYPE html>\n<html>\n<body>\n\n<h1>My First JavaScript</h1>\n\n<button type=\"button\" onclick=\"UserAction()\">\nClick me to display Date and Time.</button>\n\n<p id=\"demo\"></p>\n\n<script>\n\tfunction UserAction() {\n    var xhttp = new XMLHttpRequest();\n    xhttp.onreadystatechange = function() {\n         if (this.readyState == 4 && this.status == 200) {\n             alert(this.responseText);\n         }\n    };\n    xhttp.open(\"GET\", \"http://localhost:8154/redirect\");\n    xhttp.setRequestHeader(\"Content-type\", \"application/json\");\n    xhttp.send(\"Your JSON Data Here\");\n}\n</script>\n\n</body>\n</html> \n"})

   (GET "/redirect" []
     ;(:body (client/get "http://192.168.0.101:8080/text")))
     {:status  302
      :headers {
                ;"Access-Control-Allow-Origin"  "*"
                ;"Access-Control-Allow-Methods" "*"
                ;"Access-Control-Allow-Headers" "*"
                "Location" "http://machine:8080/text"}})
   (compojure.route/not-found "no page found"))

(defn -main [& args]
  (jetty/run-jetty
    (wrap-reload #'handler)
    {:port        8154
     :join?       false
     :async?      false
     :min-threads 1
     :max-threads 4}))

