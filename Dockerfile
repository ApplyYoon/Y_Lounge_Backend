# Build Stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon -x test

# Run Stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx1024m", "-Xms512m", "-Xss512k", "-jar", "app.jar"]
