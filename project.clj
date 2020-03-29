(defproject bff-ms-url-shortener "0.1.0-SNAPSHOT"
  :description "This project is microservice for url shortener on aws cloud."
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [compojure "1.4.0"]
                 [org.clojure/data.json "0.2.6"]
                 [uswitch/lambada "0.1.2"]
                 [com.cognitect.aws/api "0.8.456"]
                 [com.cognitect.aws/dynamodb "792.2.620.0"]
                 [com.cognitect.aws/endpoints "1.1.11.753"]]
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-exec "0.3.7"]]
  :uberjar-name "bff-ms-url-shortener.jar"
  :main ^{:skip-aot true} bff-ms-url-shortener.handler)