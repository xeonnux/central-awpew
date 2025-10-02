#!/usr/bin/env bb

;; Central AWPEW Development REPL with Babashka
;; Usage: ./repl.clj

(require '[babashka.process :as process]
         '[babashka.fs :as fs])

(println "🚀 Central AWPEW Development REPL (Babashka)")
(println "=============================================")
(println)
(println "Available functions:")
(println "  (api-start)     - Start the API server on port 3000")
(println "  (api-start 8080) - Start the API server on custom port")
(println "  (api-stop)      - Stop the API server")
(println "  (api-status)    - Check if API server process is running")
(println "  (api-health)    - Check if API is actually responding")
(println "  (api-test)      - Test the API endpoints")
(println)
(println "Ready for development! Type (api-start) to begin.")
(println)

;; Global state for the server process
(defonce server-process (atom nil))
(def pid-file ".api-server.pid")

;; Declare functions to avoid forward reference issues
(declare api-stop)

(defn api-stop
  "Stop the API server"
  []
  (when (fs/exists? pid-file)
    (let [pid-content (slurp pid-file)]
      (if (not (empty? (clojure.string/trim pid-content)))
        (let [pid (Integer/parseInt (clojure.string/trim pid-content))]
          (println "🛑 Stopping API server...")
          (try
            (process/shell "kill" (str pid))
            (fs/delete-if-exists pid-file)
            (reset! server-process nil)
            (println "✅ API server stopped")
            (catch Exception e
              (println "❌ Error stopping server:" (.getMessage e))))
          (fs/delete-if-exists pid-file)))))
  (when @server-process
    (process/destroy-tree @server-process)
    (reset! server-process nil)))

(defn api-start
  "Start the API server. Optionally specify a port: (api-start 8080)"
  ([]
   (api-start 3000))
  ([port]
   (api-stop) ; Stop any existing server
   (println (str "🚀 Starting API server on port " port "..."))
   (let [proc (process/process ["clojure" "-M:dev" "-m" "central.server.core"]
                               {:dir (str (fs/cwd))
                                :continue true})] ; Keep process running
     (reset! server-process proc)
     (Thread/sleep 2000) ; Give it time to start
     ;; Try to get PID from the process
     (let [pid (or (:pid proc) 
                   (try 
                     (-> (process/shell {:out :string} "sh" "-c" "ps aux | grep 'central.server.core' | grep -v grep | head -1 | awk '{print $2}'")
                         :out
                         clojure.string/trim)
                     (catch Exception _ nil)))]
       (when (and pid (not (empty? pid)))
         (spit pid-file pid) ; Save PID to file
         (println (str "   Process ID: " pid))))
     (println (str "✅ API server should be running at http://localhost:" port))
     (println "📡 Endpoints:")
     (println (str "   GET http://localhost:" port "/ping  - Health check"))
     (println (str "   GET http://localhost:" port "/data  - Sample data"))
     (println "🛑 Use (api-stop) to stop the server"))))

(defn api-status
  "Check if the API server is running"
  []
  (if (fs/exists? pid-file)
    (let [pid-content (slurp pid-file)]
      (if (empty? (clojure.string/trim pid-content))
        (do
          (println "❌ API server is not running (empty PID file)")
          (fs/delete-if-exists pid-file))
        (let [pid (Integer/parseInt (clojure.string/trim pid-content))]
          (try
            (let [result (process/shell {:out :string} "sh" "-c" (str "ps -p " pid " | grep -v PID"))]
              (if (and (= (:exit result) 0) (not (empty? (clojure.string/trim (:out result)))))
                (do
                  (println "✅ API server is running")
                  (println (str "   Process ID: " pid)))
                (do
                  (println "❌ API server process is dead")
                  (fs/delete-if-exists pid-file))))
            (catch Exception e
              (println "❌ Error checking process:" (.getMessage e))
              (fs/delete-if-exists pid-file))))))
    (println "❌ API server is not running (no PID file found)")))

(defn api-test
  "Test the API endpoints"
  []
  (println "🧪 Testing API endpoints...")
  (try
    (let [ping-response (process/shell {:out :string} "curl" "-s" "http://localhost:3000/ping")
          data-response (process/shell {:out :string} "curl" "-s" "http://localhost:3000/data")]
      (println "📡 /ping response:")
      (println (:out ping-response))
      (println)
      (println "📡 /data response:")
      (println (:out data-response)))
    (catch Exception e
      (println "❌ Error testing API:" (.getMessage e)))))

(defn api-health
  "Check if the API is actually responding (not just process alive)"
  []
  (println "🏥 Checking API health...")
  (try
    (let [response (process/shell {:out :string} "curl" "-s" "-w" "\n%{http_code}" "http://localhost:3000/ping")]
      (if (= (:exit response) 0)
        (let [lines (clojure.string/split (:out response) #"\n")
              http-code (last lines)
              body (clojure.string/join "\n" (drop-last lines))]
          (if (= http-code "200")
            (do
              (println "✅ API is healthy and responding")
              (println (str "   HTTP Status: " http-code))
              (println (str "   Response: " body)))
            (do
              (println "❌ API is not responding correctly")
              (println (str "   HTTP Status: " http-code)))))
        (println "❌ Failed to connect to API")))
    (catch Exception e
      (println "❌ Error checking API health:" (.getMessage e)))))

(println "💡 Pro tip: Use Ctrl+C to exit the REPL")
(println)

;; Start the interactive REPL
(require '[clojure.main :as main])
(main/repl :prompt (fn [] (print "central-awpew=> ")))
