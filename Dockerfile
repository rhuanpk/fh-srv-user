FROM maven:3.9.6-sapmachine-21 AS build
WORKDIR /home/app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21
WORKDIR /usr/local/lib
COPY --from=build /home/app/target/user-0.0.1-SNAPSHOT.jar app-user.jar
CMD ["java", "-jar", "app-user.jar"]