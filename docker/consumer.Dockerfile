FROM openjdk:11-jre
WORKDIR /env/consumer
ADD target/*.jar ags-poc-1.0.jar
ENTRYPOINT ["java", "-jar", "ags-poc-1.0.jar"]
