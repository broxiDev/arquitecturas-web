FROM maven:3.9-eclipse-temurin-21 AS build
ARG MODULE
WORKDIR /build
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY discovery-service/pom.xml discovery-service/pom.xml
COPY fridge-service/pom.xml fridge-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY kitchen-service/pom.xml kitchen-service/pom.xml
COPY recommendation-service/pom.xml recommendation-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY audit-service/pom.xml audit-service/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
RUN mvn -q -pl ${MODULE} -am dependency:go-offline
COPY ${MODULE}/src ${MODULE}/src
RUN mvn -q -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
ARG MODULE
WORKDIR /app
COPY --from=build /build/${MODULE}/target/${MODULE}-*.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
