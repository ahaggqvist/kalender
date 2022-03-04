FROM gcr.io/distroless/java17:nonroot AS app-build

LABEL maintainer="ahaggqvist@gmail.com"

WORKDIR /app

COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM gcr.io/distroless/java:11
USER nonroot

WORKDIR /app

COPY --from=app-build /app/dependencies/ ./
COPY --from=app-build /app/spring-boot-loader/ ./
COPY --from=app-build /app/snapshot-dependencies/ ./
COPY --from=app-build /app/application/ ./

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:InitialRAMPercentage=45 -XX:MaxRAMPercentage=90 -XX:+CrashOnOutOfMemoryError -Djavax.accessibility.assistive_technologies="" -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Dcom.sun.management.jmxremote=true -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]