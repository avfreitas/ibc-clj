(defproject ibc-clj "0.1.0-SNAPSHOT"

  :description "API - IBC Backend com Compojure e Ring"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [compojure "1.7.1"]

                 [ring/ring-defaults "0.3.3"]
                 [ring/ring-json "0.5.0"]
                 [ring-cors "0.1.13"]
                 [ring/ring-servlet "1.9.0"]
                                 
                 [mysql/mysql-connector-java "8.0.26"]
                 [com.zaxxer/HikariCP "5.0.1"]

                 [com.github.seancorfield/next.jdbc "1.2.796"]
                 [cheshire "5.10.1"]
                 
                 [org.slf4j/slf4j-simple "1.7.32"]]

  :plugins [[lein-ring "0.12.5"]
            [lein-uberwar "0.2.1"]]

  :ring {:handler ibc-clj.core/app}
  :uberwar {:handler ibc-clj.core/app}
  :profiles {:uberjar {:aot :all}}
  :main ^:skip-aot ibc-clj.core)
