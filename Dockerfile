# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle/libs.versions.toml gradle/

# Copy server module
COPY server server

# Build the server module
# We use --no-daemon to save memory in the build environment
RUN ./gradlew :server:installDist --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the build artifacts from the build stage
COPY --from=build /app/server/build/install/server /app/server

# Expose the port (Railway will provide the PORT env var)
EXPOSE 8080

# Run the server
CMD ["./server/bin/server"]
