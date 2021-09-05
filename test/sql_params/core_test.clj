(ns sql-params.core-test
  (:require [sql-params.core :as sut]
            [clojure.test :as t]))


(t/deftest simple-query
  (t/is (=
         (sut/convert
          ["SELECT * FROM foo
            WHERE x = :x
            AND y=:y::TEXT
            AND z>:y"
           {:x "xs" :y 1}])
         
         ["SELECT * FROM foo WHERE x = ? AND y=?::TEXT AND z>?" "xs" 1 1])))


(t/deftest nested-params
  (t/is (=
         (sut/convert
          ["SELECT * FROM foo
            WHERE a=:foo.a
            AND b=:foo.b.value"
           {:foo {:a 1 :b {:value 2}}}])
         
         ["SELECT * FROM foo WHERE a=? AND b=?" 1 2])))
