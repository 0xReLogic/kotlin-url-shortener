# ---- Build Stage ----
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Set environment variables (can be overridden)
ENV PORT=8080
ENV BASE_URL=http://localhost:8080
ENV DATABASE_URL=jdbc:h2:/data/shortener;DB_CLOSE_DELAY=-1;
ENV DATABASE_DRIVER=org.h2.Driver
ENV DATABASE_USER=sa
ENV DATABASE_PASSWORD=

# Expose port
EXPOSE 8080

# Create persistent volume for H2
VOLUME ["/data"]

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]