# 后端镜像：复制本地打好的 jar
FROM eclipse-temurin:17-jre
ENV TZ=Asia/Shanghai
WORKDIR /app
COPY target/Personal_Blog-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8008
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]