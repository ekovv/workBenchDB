FROM openjdk:20-jdk

WORKDIR /workBenchD
COPY . /workBenchD

CMD ["java", "-jar", "target/workBenchDB-0.0.1-SNAPSHOT.jar"]
