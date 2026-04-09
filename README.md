# Smart Campus API

A JAX-RS RESTful API for managing Rooms and Sensors in a Smart Campus environment.

## Technology Stack
- Java 21
- JAX-RS (Jersey 3.1.3)
- Grizzly HTTP Server
- Maven

## How to Build and Run

### Prerequisites
- Java 21
- Maven 3.9+

### Build
```bash
mvn clean compile
```

### Run
```bash
mvn exec:java "-Dexec.mainClass=com.smartcampus.Main"
```

The API will start at: `http://localhost:8080/api/v1`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1/discovery | API discovery and metadata |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a new room |
| GET | /api/v1/rooms/{id} | Get a specific room |
| DELETE | /api/v1/rooms/{id} | Delete a room |
| GET | /api/v1/sensors | Get all sensors |
| POST | /api/v1/sensors | Create a new sensor |
| GET | /api/v1/sensors/{id}/readings | Get sensor readings |
| POST | /api/v1/sensors/{id}/readings | Add a sensor reading |

## Sample curl Commands

### 1. Discovery
```bash
curl http://localhost:8080/api/v1/discovery
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"name":"Library","capacity":50}'
```

### 3. Create a Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"type":"CO2","status":"ACTIVE","currentValue":450.0,"roomId":"YOUR_ROOM_ID"}'
```

### 4. Add a Sensor Reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/YOUR_SENSOR_ID/readings \
  -H "Content-Type: application/json" \
  -d '{"value":523.5}'
```

### 5. Filter Sensors by Type
```bash
curl http://localhost:8080/api/v1/sensors?type=CO2
```

## Report

### Part 1 - JAX-RS Lifecycle & HATEOAS

**Q: Explain the default lifecycle of a JAX-RS Resource class and thread safety.**

By default, JAX-RS creates a new instance of a resource class for every incoming HTTP request. This is called request-scoped lifecycle. Because each request gets its own instance, instance variables are thread-safe. However, since we use a shared static DataStore (HashMap), we must be careful about concurrent access. In a production system, we would use ConcurrentHashMap or synchronization to prevent race conditions.

**Q: Why is HATEOAS considered a hallmark of advanced RESTful design?**

HATEOAS (Hypermedia as the Engine of Application State) means the API response includes links to related resources. This benefits client developers because they do not need to hardcode URLs — they can discover them dynamically from responses. It makes the API self-documenting and reduces coupling between client and server.

### Part 2 - Room Management

**Q: Returning only IDs versus full room objects in list responses.**

Returning only IDs reduces network bandwidth and is faster for large datasets. However, the client must make additional requests to get details. Returning full objects increases payload size but reduces the number of requests. For our Smart Campus API, we return full objects for simplicity, which is acceptable for moderate data sizes.

**Q: Is DELETE idempotent in your implementation?**

Yes, DELETE is idempotent. The first call deletes the room and returns 204 No Content. Subsequent calls return 404 Not Found. In both cases, the end state is the same — the room does not exist. This satisfies the idempotency requirement of HTTP DELETE.

### Part 3 - Sensors

**Q: Consequences of @Consumes mismatch.**

If a client sends data in text/plain or application/xml when the endpoint expects application/json, JAX-RS returns a 415 Unsupported Media Type response automatically. The resource method is never invoked.

**Q: @QueryParam vs path parameter for filtering.**

Query parameters like ?type=CO2 are better for filtering because they are optional — the endpoint works with or without them. Path parameters like /sensors/type/CO2 imply the type is a required identifier, which is semantically incorrect for filtering. Query parameters are the REST standard for search and filter operations.

### Part 4 - Sub-Resources

**Q: Benefits of Sub-Resource Locator pattern.**

The sub-resource locator pattern delegates handling of nested paths to separate classes. This improves code organisation, separation of concerns, and maintainability. For large APIs, putting all logic in one class becomes unmanageable. By delegating /sensors/{id}/readings to SensorReadingResource, each class has a single responsibility.

### Part 5 - Error Handling

**Q: Why is HTTP 422 more accurate than 404 for missing roomId?**

404 means the requested URL resource was not found. 422 Unprocessable Entity means the request was well-formed but contained invalid data. When a client POSTs a sensor with a non-existent roomId, the URL /api/v1/sensors is valid — the problem is inside the request body. Therefore 422 is semantically more accurate.

**Q: Cybersecurity risks of exposing stack traces.**

Stack traces reveal internal class names, method names, file paths, line numbers, and library versions. Attackers can use this to identify vulnerable dependencies, understand the application structure, and craft targeted attacks. Our GlobalExceptionMapper prevents this by returning only a generic error message.

**Q: Why use filters for logging instead of manual logging?**

Filters implement cross-cutting concerns in one place. If we added Logger.info() to every method, we would duplicate code across dozens of methods. Filters are applied automatically to every request and response, ensuring consistent logging without modifying business logic.