FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia os arquivos de build
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY src ./src

# Compila o projeto
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# Roda a aplicação
ENTRYPOINT ["java", "-jar", "target/wowmarket-0.0.1-SNAPSHOT.jar"]