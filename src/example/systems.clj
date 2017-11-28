(ns example.systems
  (:require [system.core :refer [defsystem]]
            [ring.middleware.defaults :refer [site-defaults api-defaults wrap-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [system.components.immutant-web :refer [new-immutant-web]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [environ.core :refer [env]]
            [example.handler :refer [site-routes api-routes]]
            [example.not-found :refer [wrap-not-found]]
            [example.html :refer [not-found]]
            [com.stuartsierra.component :as component]))

(defsystem dev-system
  [:middleware (new-middleware {:middleware [[wrap-not-found (not-found)]]})
   :site-middleware (new-middleware {:middleware [[wrap-defaults site-defaults]]})
   :api-middleware (new-middleware {:middleware [wrap-restful-format [wrap-defaults api-defaults]]})
   :site-routes (component/using (new-endpoint site-routes) [:site-middleware])
   :api-routes (component/using (new-endpoint api-routes) [:api-middleware])
   :site-handler (component/using (new-handler) [:site-routes :middleware])
   :api-handler (component/using (new-handler) [:api-routes])
   :site-server (component/using (new-immutant-web :port (Integer. (env :http-port))) [:site-handler])
   :api-server (component/using (new-immutant-web :port (Integer. (env :http-port)) :options {:path "/api"}) [:api-handler])])

(def prod-system dev-system)


 


