
# Parking Lot System - Spring Boot (Runnable)

How to run:
1. Ensure Java 17+ and Maven are installed.
2. Build: `mvn clean package`
3. Run: `mvn spring-boot:run`
4. H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:parkingdb)
5. Swagger UI: http://localhost:8080/swagger-ui/index.html


APIs (examples):
- Admin: POST /admin/slot to add slots (requires ADMIN role)
- GET /admin/slots
- DELETE /admin/slot/{id}
- POST /api/pricing -> body : { "vehicleType": "Bicycle", "freeMinutes": 90, "pricePerHour": 5.0 }
- GET /api/pricing 
- PUT /api/pricing/{id}
- DELETE /api/pricing/{id}
- POST /api/parking/entry  -> body: { "plateNumber": "KA01AA1111", "type": "CAR", "ownerName": "Mohit" }
- POST /api/parking/prepareExit/{ticketId}
- POST /api/parking/pay/{ticketId} -> body: { "simulateSuccess": true }