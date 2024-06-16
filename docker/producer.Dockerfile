FROM openjdk:11-jre
WORKDIR /env/producer
ADD target/*.jar ags-poc-1.0.jar
ENTRYPOINT ["java", "-jar", "ags-poc-1.0.jar"]
