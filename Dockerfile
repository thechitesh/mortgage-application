FROM amazoncorretto:17-alpine
LABEL maintainer="thechitesh@gmail.com"
# Set the working directory
WORKDIR /app
COPY mortgage-service/target/mortgage-service-0.0.1-SNAPSHOT.jar /app/mortgage-api-1.0.0.jar
EXPOSE 1009
ENTRYPOINT ["java","-jar","/app/mortgage-api-1.0.0.jar"]
