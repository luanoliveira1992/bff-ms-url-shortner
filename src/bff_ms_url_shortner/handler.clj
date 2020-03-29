(ns bff-ms-url-shortener.handler
  (:require [uswitch.lambada.core :refer [deflambdafn]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [bff-ms-url-shortener.controller :refer [create-flag-by-url get-url-by-flag]])
  (:gen-class))

  ;;
(defn handle-create
  [event]
  (let [body (get event :body)
        url (get body :url)
        redirect_url (create-flag-by-url url)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body {:url redirect_url}}))

(defn handle-get
  [event]
  (let [body (get event :body)
        token (get body :token)
        response (get-url-by-flag token)
        url (get response :url)]
    {:status 302
     :headers {"Location" url}})
  {:status 302})

(deflambdafn bff-ms-url-shortener.CreateShotnerFn
  [in out ctx]
  (let [event (json/read (io/reader in))
        res (handle-get event)]
    (with-open [w (io/writer out)]
      (json/write res w))))

(deflambdafn bff-ms-url-shortener.GetShotnerFn
  [in out ctx]
  (let [event (json/read (io/reader in))
        res (handle-create event)]
    (with-open [w (io/writer out)]
      (json/write res w))))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCalling Lambda Event")
  (handle-get {:body {:token "abcde"}}))