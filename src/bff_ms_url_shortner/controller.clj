(ns bff-ms-url-shortener.controller
  (:require [compojure.core :refer :all]
            [clojure.java.io :as io]
            [cognitect.aws.client.api :as aws]
            [clojure.data.json :as json]))

;; Defining the dynamo client
(def ddb (aws/client {:api :dynamodb
                      :region "us-east-1"}))


;; Make up a map in struct to insert an Item in dynamo, linking the a flag to an ur


(defn make-body-item-to-insert [flag url]
  (let [item (format "{\"shortener-dlabs-url\" : [{\"PutRequest\" : {\"Item\" : {\"token\" : \"%s\" , \"url\" : \"%s\" }}}]}" flag url)]
    (json/read-str item :key-fn keyword)))

;; Generate randoly a flag with 10 chars
(defn generate-flag []
  (apply str (take 10 (repeatedly #(char (+ (rand 26) 65))))))

;; Given an url, creates a flag for, and save this in dynamo
(defn create-flag-by-url [url]
  (let [flag (generate-flag)
        item (make-body-item-to-insert flag url)]
    (aws/invoke ddb {:op   :BatchWriteItem :request {:RequestItem item}})
    flag))

(defn map-vals [f m]
  (->> m
       (map (fn [[k v]] [k (f v)]))
       (into (empty m))))

(defn make-dynamodb-value-spec [v]
  (cond
    (string? v) {:S v}
    (number? v) {:N (str v)}
    (bytes?  v) {:B v}
    :else (throw (ex-info "unknown value type"
                          {:error-type ::unknown-value-type
                           :error-info {:value-type (type v)}}))))

(defn input-stream-to-byte-array [input-stream]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy input-stream out)
    (.toByteArray out)))

(defn parse-dynamodb-value-spec [spec]
  (let [k (first (keys spec))
        v (get spec k)]
    (case k
      :S v
      :N (Long/parseLong v)
      :B (cond-> v (instance? java.io.InputStream v) input-stream-to-byte-array)
      (throw (ex-info "unknown value type"
                      {:error-type ::unknown-value-type
                       :error-info {:value-type k}})))))

;; Given a flag, look for in dynamoDB your url, then response with this
(defn get-url-by-flag [flag]
  (let [item (aws/invoke ddb
                         {:op :GetItem
                          :request {:TableName "shortener-dlabs-url"
                                    :Key (map-vals make-dynamodb-value-spec
                                                   {"token" flag})
                                    :ConsistentRead true}})]
    (when-let [item-specs (not-empty (:Item item))]
      (map-vals parse-dynamodb-value-spec item-specs))))


