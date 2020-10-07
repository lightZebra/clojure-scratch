(defproject clojure-scratch "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "1.2.603"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [clj-http "3.9.0"]
                 [cheshire "5.8.0"]
                 [com.google.apis/google-api-services-bigquery "v2-rev256-1.21.0"]
                 [prismatic/schema "1.1.9"]
                 [liberator "0.15.2"]
                 [ring-middleware-format "0.7.2" :exclusions [cheshire ring/ring-core]]
                 [compojure "1.6.1" :exclusions [ring/ring-core]]
                 [http-kit "2.2.0"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [ring "1.7.0-RC1" :exclusions [ring/ring-codec clj-time]]
                 [javax.servlet/servlet-api "2.5"]
                 [ring/ring-json "0.4.0" :exclusions [cheshire]]
                 [ring/ring-jetty-adapter "1.7.0-RC1" :exclusions [clj-time ring/ring-core]]
                 [clj-time "0.15.1"]
                 [com.taoensso/carmine "2.18.1" :exclusions [org.clojure/tools.reader com.taoensso/encore]]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/core.cache "0.8.2"]]
  :repl-options {:init-ns clojure-scratch.core})
