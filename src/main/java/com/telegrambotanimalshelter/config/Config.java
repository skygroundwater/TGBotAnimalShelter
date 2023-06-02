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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;

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
    public Logger getLogger(){
        return LoggerFactory.getLogger(AnimalShelterBotListener.class);
    }

    @Bean
    @Scope(scopeName = "singleton")
    public Shelter dogShelter() {
        ArrayList<Dog> dogs = new ArrayList<>();
        return new Shelter(
                "Halfway Home",
                "Приют работает с 2019 года. В приоритете — спасение сбитых машинами и больных животных. \n" +
                        "За период своей деятельности волонтеры спасли более 80 собак, но удалось пристроить только четверых.\n" +
                        " Остальные ждут свой дом, где они будут считаться членами семьи. \n" +
                        "Все собаки в приюте имеют паспорта, привиты и кастрированы. В социальных сетях приют старается повышать\n" +
                        " осведомленность людей о важности вакцинации, стерилизации, гуманного отношения к животным. \n" +
                        "Приют старается повысить осведомленность людей о важности содержания собак в вольерах,\n" +
                        " так как на цепи собака становится дикой, раздражительной, недоверчивой,\n" +
                        " подавленной и агрессивной по отношению к хозяину.",
                "Мы работаем круглосуточно и без выходных. Найти нас можно по адресу: город Астана, Улица Жұмекен Нәжімеденов, 4, КВ 1803",
                "На территории приюта будьте осторожны",
                "Контактные номера для предварительного оформления пропуска: 43545234657",
                dogs, ShelterType.DOGS_SHELTER
        );
    }

    @Bean
    @Scope(scopeName = "singleton")
    public Shelter catShelter() {
        ArrayList<Cat> cats = new ArrayList<>();
        return new Shelter("Ковчег",
                "В столице появился настоящий «кошкин дом», где живут около 60 пушистых питомцев.\n" +
                        " Все обитатели приюта с трудной судьбой: одних нашли на улице, других подобрали в подвале,\n" +
                        " есть и те, от которых отказались хозяева. Здесь за представителями семейства кошачьих не только\n" +
                        " ухаживают и кормят, но и лечат, ставят прививки, а также подыскивают им новых хозяев.\n" +
                        " Всего за один месяц работы приюта свой новый дом нашли 20 животных.",
                "Мы работаем Вт-Вс: с 13.00 до 20.00ч, понедельник выходной. Найти нас можно по адресу: Г.АСТАНА, РАЙОН САРЫАРКА, улица Степан Разин, дом 15",
                "На территории приюта будьте осторожны",
                "Контактные номера для предварительного оформления пропуска: 8 705 763 87 76",
                cats, ShelterType.CATS_SHELTER);
    }
}