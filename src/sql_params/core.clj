(ns sql-params.core
  (:require [clojure.string :as s]
            [clojure.set :as st]))


(def param-regex  #"(?<!:):([-a-zA-Z\.]+)")


(defn- replace-params [sql-string]
  (s/replace sql-string
             param-regex
             "?"))


;; TODO: handle list values
;; e.g. {:ids [1, 2, 5, 9]}
(defn- param-map->vector [m params]
  (mapv #(get-in m %) params))


(defn- extract-params [sql-string]
  (map (fn [[_ param]] (map keyword (s/split param #"\.")))
       (re-seq param-regex sql-string)))


(defn whitespacify [s]
  (s/replace s #"\s+" " "))


;; TODO: fix!
(defn verify-param-map [param-map params]
  (let [missing-keys (clojure.set/difference (into #{} params) 
                                             (into #{} (keys param-map)))]
    (assert (empty? missing-keys) (str "SQL parameter(s) missing: " missing-keys))))


(defn convert [[sql-string param-map]]
  (let [params (extract-params sql-string)]
    ;;(verify-param-map param-map params)
    (into [(whitespacify (replace-params sql-string))] 
          (param-map->vector param-map params))))




(comment

  (convert   ["SELECT * FROM foo
            WHERE x = :x
            AND y=:y::TEXT
            AND z>:y"
           {:x "xs" :y 1}] )

  )
