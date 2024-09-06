## Stage 1 : unpack fat-jar
FROM amazoncorretto:21 as builder

USER root
COPY build/libs/arc-spring-init-1.0.0.jar /deployments/app.jar
WORKDIR /deployments
RUN java -Djarmode=layertools -jar app.jar extract

## Stage 2 : create image
FROM amazoncorretto:21
USER 185
WORKDIR /deployments
COPY --from=builder /deployments/dependencies/ ./
COPY --from=builder /deployments/spring-boot-loader/ ./
COPY --from=builder /deployments/application/ ./
COPY config ./config
COPY agents ./agents

EXPOSE 8080
EXPOSE 9090
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]