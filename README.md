
# Parking Lot System - Spring Boot (Runnable)

How to run:
1. Ensure Java 17+ and Maven are installed.
2. Build: `mvn clean package`
3. Run: `mvn spring-boot:run` or `java -jar target/parking-lot-system-0.0.1-SNAPSHOT.jar`
4. H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:parkingdb)
5. Swagger UI: http://localhost:8080/swagger-ui/index.html

Notes & Assumptions / Remaining items:
- OAuth2 (Google) is supported via Spring Security, but to run with Google you must add OAuth client credentials in application.yml or environment variables. For quick local testing set `app.auth.enabled=false` in application.yml to use in-memory users (`admin` / `user`).
- Payments are simulated. The `/api/parking/pay/{ticketId}` accepts JSON `{ "simulateSuccess": true }` to simulate success.
- Concurrency: slot allocation uses PESSIMISTIC_WRITE lock in repository and @Transactional to avoid double allocation under concurrent requests.
- Pricing rules: implemented simple policy: first 2 hours free, then per-hour rates. Admin endpoints to change pricing are NOT implemented (you can extend PricingService to make rates configurable).
- Postman collection included as `postman_collection.json`.
- Tests are minimal; add unit/integration tests as needed.

APIs (examples):
- POST /api/parking/entry  -> body: { "plateNumber": "KA01AA1111", "type": "CAR", "ownerName": "Mohit" }
- POST /api/parking/prepareExit/{ticketId}
- POST /api/parking/pay/{ticketId} -> body: { "simulateSuccess": true }
- Admin: POST /admin/slot to add slots (requires ADMIN role)
- GET /admin/slots

What is left / possible improvements:
- Make pricing rules editable by ADMIN (DB entity + endpoints).
- Implement OAuth2 Google client id/secret in application.yml for real login.
- Add auditing, metrics, and better error handling responses.
- Add more unit & integration tests.
