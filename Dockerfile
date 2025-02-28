# Build stage
FROM maven:3.9.5-eclipse-temurin-21 AS builder  

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY . ./

RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jdk  
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
