package com.telegrambotanimalshelter.config;

import com.pengrad.telegrambot.TelegramBot;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.listener.AnimalShelterBotListener;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;

import static com.telegrambotanimalshelter.utils.Constants.*;

@Configuration
@Data
public class Config {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.owner}")
    Long ownerId;

    @Bean
    public TelegramBot telegramBot(@Value("${bot.token}") String token) {
        return new TelegramBot(token);
    }

    @Bean
    @Scope(scopeName = "prototype")
    public Logger getLogger() {
        return LoggerFactory.getLogger(AnimalShelterBotListener.class);
    }

    @Bean
    @Scope(scopeName = "singleton")
    public Shelter dogShelter() {
        ArrayList<Dog> dogs = new ArrayList<>();
        return new Shelter(
                dogShelterName,
                DOG_SHELTER_DESCRIPTION,
                DOG_SHELTER_WORKING_HOURS,
                DOG_SHELTER_SAFETY,
                DOG_SHELTER_SECURITY_CONTACTS,
                DOG_ACQUAINTANCE,
                CONTRACT_DOCUMENTS,
                DOG_TRANSPORTATION,
                HOME_FOR_PUPPY,
                HOME_FOR_ADULT_DOG,
                HOME_FOR_RESTRICTED_DOG,
                dogs, ShelterType.DOGS_SHELTER
        );
    }

    @Bean
    @Scope(scopeName = "singleton")
    public Shelter catShelter() {
        ArrayList<Cat> cats = new ArrayList<>();
        return new Shelter(catShelterName,
                CAT_SHELTER_DESCRIPTION,
                CAT_SHELTER_WORKING_HOURS,
                CAT_SHELTER_SAFETY,
                CAT_SHELTER_SECURITY_CONTACTS,
                CAT_ACQUAINTANCE,
                CONTRACT_DOCUMENTS,
                CAT_TRANSPORTATION,
                HOME_FOR_KITTY,
                HOME_FOR_ADULT_CAT,
                HOME_FOR_RESTRICTED_CAT,
                cats, ShelterType.CATS_SHELTER);
    }
}