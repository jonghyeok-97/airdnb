FROM openjdk:17-jdk

COPY build/libs/*SNAPSHOT.jar airdnb.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "airdnb.jar"]