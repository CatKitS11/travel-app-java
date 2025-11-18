# server/Dockerfile (เปลี่ยนชื่อเป็น Dockerfile)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
# ✅ ใช้ PORT จาก environment variable (Render จะ set ให้อัตโนมัติ)
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-8081}"
COPY --from=build /app/target/*.jar /app/app.jar
# ✅ Expose port (Render จะใช้ PORT env var)
EXPOSE ${PORT:-8081}
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]