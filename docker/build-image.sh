cd ..
mvn clean install -DskipTests
cd docker
docker-compose build