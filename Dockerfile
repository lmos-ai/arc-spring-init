## SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
##
## SPDX-License-Identifier: Apache-2.0
## Stage 1 : unpack fat-jar
FROM amazoncorretto:21 as builder

USER root
COPY build/libs/arc-spring-init-1.0.0.jar /deployments/app.jar
WORKDIR /deployments
RUN java -Djarmode=layertools -jar app.jar extract
# Ensure that the snapshot-dependencies folder is created even if there were no snapshot dependencies in the jar file (to avoid error when copying from that directory below)
RUN mkdir -p snapshot-dependencies

## Stage 2 : create image
FROM amazoncorretto:21
USER 185
WORKDIR /deployments
COPY --from=builder /deployments/dependencies/ ./
COPY --from=builder /deployments/snapshot-dependencies/ ./
COPY --from=builder /deployments/spring-boot-loader/ ./
COPY --from=builder /deployments/application/ ./
COPY config ./config
COPY agents ./agents

EXPOSE 8080
EXPOSE 9090
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]