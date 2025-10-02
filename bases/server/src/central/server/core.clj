(ns central.server.core
  (:require [central.api.interface :as api]
            [central.db.interface :as db]
            [central.core.interface :as core])
  (:gen-class))

(defn -main [& args]
  (println "Starting central server...")
  (println "API interface loaded:" (str 'central.api.interface))
  (println "DB interface loaded:" (str 'central.db.interface))
  (println "Core interface loaded:" (str 'central.core.interface))
  
  ;; Start the HTTP API server
  (api/start-server 3000)
  
  (println "Server started successfully!")
  (println "API available at: http://localhost:3000")
  (println "Endpoints:")
  (println "  GET /ping - Health check")
  (println "  GET /data - Sample data")
  
  ;; Keep the server running
  (println "Press Ctrl+C to stop the server")
  (try
    (while true
      (Thread/sleep 1000))
    (catch InterruptedException _
      (println "\nShutting down server...")
      (api/stop-server)
      (println "Server stopped."))))
