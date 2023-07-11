FROM openjdk:17
ADD /target/TGBotAnimalShelter-0.0.1-SNAPSHOT.jar shelter_bot_backend.jar
ENTRYPOINT ["java", "-jar", "shelter_bot_backend.jar"]
