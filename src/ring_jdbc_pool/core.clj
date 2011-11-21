(ns ring-jdbc-pool.core
  (:require [clojure.java.jdbc :as jdbc]))

(defn- make-data-source* [{:keys [classname subprotocol subname user password]}]
  {:datasource
   (doto (com.mchange.v2.c3p0.ComboPooledDataSource.)
     (.setDriverClass classname)
     (.setJdbcUrl (str "jdbc:" subprotocol ":" subname))
     (.setUser user)
     (.setPassword password)
     (.setMaxIdleTimeExcessConnections (* 30 60))
     (.setMaxIdleTime (* 2 60 60)))})

(def make-data-source (memoize make-data-source*))

(defn wrap-jdbc-connection [handler db-spec]
  (let [data-source (make-data-source db-spec)]
    (fn [request] (jdbc/with-connection data-source (handler request)))))
