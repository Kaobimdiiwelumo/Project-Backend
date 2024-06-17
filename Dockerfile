FROM eclipse-temurin:22-jdk-alpine
VOLUME /tmp
COPY target/FinalYear-0.0.1-SNAPSHOT.jar ascii-art-generator.jar
ENTRYPOINT ["java","-jar","/ascii-art-generator.jar"]
