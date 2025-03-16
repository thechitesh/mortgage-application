#!/bin/sh

echo "The current working directory $var"
var=$(pwd)

echo "Going to build the project using maven build tool"
mvn clean install
if docker ps -f name=prometheus
then
  echo "Going to stop the running containers"
  docker-compose down
fi
docker-compose up -d
echo "Going to start the Mortgage Service application"
java -jar $var/mortgage-service/target/mortgage-service-0.0.1-SNAPSHOT.jar
