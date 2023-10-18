./mvnw package
docker-compose -f docker/docker-compose-dev-remote.yml up --build
