(ns central.api.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.util.response :refer [response]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn ping-handler
  "Health check endpoint"
  [request]
  (response {:status "ok" 
             :message "pong" 
             :timestamp (str (java.time.Instant/now))}))

(defn data-handler
  "Sample data endpoint"
  [request]
  (response {:data [{:id 1 :name "Sample Item 1" :value 100}
                    {:id 2 :name "Sample Item 2" :value 200}
                    {:id 3 :name "Sample Item 3" :value 300}]
             :count 3
             :timestamp (str (java.time.Instant/now))}))

(defroutes api-routes
  (GET "/ping" [] ping-handler)
  (GET "/data" [] data-handler)
  (route/not-found {:error "Not found"}))

(def app
  (-> api-routes
      (wrap-json-response)
      (wrap-json-body {:keywords? true})
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post :put :delete :options]
                 :access-control-allow-headers ["Content-Type" "Authorization"])))

(defonce server (atom nil))

(defn stop-server
  "Stop the HTTP server"
  []
  (when @server
    (println "Stopping API server...")
    (.stop @server)
    (reset! server nil)
    (println "API server stopped")))

(defn start-server
  "Start the HTTP server on the specified port"
  ([]
   (start-server 3000))
  ([port]
   (when @server
     (stop-server))
   (println (str "Starting API server on port " port "..."))
   (reset! server (run-jetty app {:port port :join? false}))
   (println (str "API server started at http://localhost:" port))
   @server))
