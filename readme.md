# Central AWPEW

**Air and Water Pathogen Early Warning Central Server**

A Clojure-based API server built with the Polylith architecture, designed for monitoring and early warning systems for air and water pathogen detection. Features a REST API with health checks and data endpoints for environmental monitoring applications.

## 🏗️ Architecture

This project follows the [Polylith](https://polylith.gitbook.io/polylith) architecture pattern, organizing code into:

- **Components**: Reusable business logic modules
  - `api` - HTTP API routes and middleware for pathogen data
  - `core` - Core business logic for early warning algorithms
  - `db` - Database interface for environmental data storage
- **Bases**: Entry points that expose public APIs
  - `server` - Main server application for pathogen monitoring
- **Projects**: Deployable artifacts
  - `central-awpew` - Main project configuration for the early warning system

## 🚀 Quick Start

### Prerequisites

- [Clojure](https://clojure.org/guides/install_clojure) 1.11.1+
- [Babashka](https://babashka.org/) (for development REPL)

### Development

1. **Start the development REPL:**
   ```bash
   ./repl.clj
   ```

2. **Start the API server:**
   ```clojure
   central-awpew=> (api-start)
   ```

3. **Test the API:**
   ```clojure
   central-awpew=> (api-health)
   central-awpew=> (api-test)
   ```

4. **Stop the server:**
   ```clojure
   central-awpew=> (api-stop)
   ```

### Alternative: Direct Clojure

```bash
# From workspace root
clj -M:dev -m central.server.core

# Or from project directory
cd projects/central-awpew
clj -M:main
```

## 📡 API Endpoints

The server runs on `http://localhost:3000` by default.

### Health Check
```bash
GET /ping
```
Returns server status and timestamp for monitoring system health:
```json
{
  "status": "ok",
  "message": "pong",
  "timestamp": "2025-10-02T19:15:34.114757Z"
}
```

### Pathogen Data
```bash
GET /data
```
Returns sample pathogen monitoring data with metadata:
```json
{
  "data": [
    {"id": 1, "name": "Air Sample Station A", "pathogen_level": 0.02, "location": "Building 1"},
    {"id": 2, "name": "Water Sample Station B", "pathogen_level": 0.01, "location": "Treatment Plant"},
    {"id": 3, "name": "Air Sample Station C", "pathogen_level": 0.05, "location": "Building 2"}
  ],
  "count": 3,
  "timestamp": "2025-10-02T19:15:34.114757Z"
}
```

## 🛠️ Development Tools

### Babashka REPL Functions

- `(api-start)` - Start the API server on port 3000
- `(api-start 8080)` - Start on a custom port
- `(api-stop)` - Stop the API server
- `(api-status)` - Check if server process is running
- `(api-health)` - Check if API is responding (HTTP 200)
- `(api-test)` - Test both endpoints

### Project Structure

```
central-awpew/
├── components/           # Reusable business logic
│   ├── api/             # HTTP API routes for pathogen data
│   ├── core/            # Early warning algorithms and logic
│   └── db/              # Environmental data storage interface
├── bases/               # Entry points
│   └── server/          # Main pathogen monitoring server
├── projects/            # Deployable artifacts
│   └── central-awpew/   # Early warning system project
├── repl.clj            # Development REPL script
└── deps.edn            # Workspace dependencies
```

## 🔧 Configuration

### Dependencies

The project uses:
- **Ring** - HTTP server abstraction
- **Compojure** - Routing library
- **Jetty** - HTTP server implementation
- **CORS** - Cross-origin resource sharing

### Port Configuration

Default port is 3000. To change:
```clojure
(api-start 8080)  ; Start on port 8080
```

## 🧪 Testing

Test the API endpoints:
```bash
# Health check
curl http://localhost:3000/ping

# Sample data
curl http://localhost:3000/data

# Pretty print with jq
curl -s http://localhost:3000/ping | jq .
```

## 📚 Polylith Resources

- [Polylith Documentation](https://polylith.gitbook.io/polylith)
- [Poly Tool Documentation](https://cljdoc.org/d/polylith/clj-poly/CURRENT)
- [RealWorld Example](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)
- [Polylith Slack](https://clojurians.slack.com/archives/C013B7MQHJQ)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test with `(api-test)`
5. Submit a pull request

## 📄 License

[Add your license information here]
