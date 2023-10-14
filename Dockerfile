FROM maven:3.8.3-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/cloud-file-storage-0.0.1-SNAPSHOT.jar ./cloud-file-storage.jar

CMD ["java", "-jar", "cloud-file-storage.jar"]
