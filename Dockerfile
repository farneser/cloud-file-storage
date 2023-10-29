FROM maven:3.9.4 AS build

COPY src /app/src

COPY checkstyle.xml /app/checkstyle.xml

COPY pom.xml /app

ENV POSTGRES_HOST ${POSTGRES_HOST}
ENV POSTGRES_PORT ${POSTGRES_PORT}
ENV POSTGRES_DB ${POSTGRES_DB}
ENV POSTGRES_USERNAME ${POSTGRES_USERNAME}
ENV POSTGRES_PASSWORD ${POSTGRES_PASSWORD}
ENV REDIS_HOST ${REDIS_HOST}
ENV REDIS_PORT ${REDIS_PORT}

RUN mvn -f /app/pom.xml clean package -Dcheckstyle.skip=true -DskipTests

FROM eclipse-temurin:17

COPY --from=build /app/target/cloud-file-storage-0.0.1-SNAPSHOT.jar /usr/local/lib/cloud-file-storage.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/usr/local/lib/cloud-file-storage.jar"]