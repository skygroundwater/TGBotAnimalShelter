package com.telegrambotanimalshelter.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.service.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.service.petservice.PetService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AnimalShelterBotListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final PetService catsService;

    private final PetService dogsService;

    private final PetOwnersService petOwnersService;

    private final Logger logger = LoggerFactory.getLogger(AnimalShelterBotListener.class);


    public AnimalShelterBotListener(TelegramBot telegramBot,
                                    @Qualifier("catsServiceImpl") PetService catsService,
                                    @Qualifier("dogsServiceImpl") PetService dogsService,
                                    PetOwnersService petOwnersService) {
        this.telegramBot = telegramBot;
        this.catsService = catsService;
        this.dogsService = dogsService;
        this.petOwnersService = petOwnersService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    @Override
    public int process(List<Update> updates) {
        try {
            updates.stream()
                    .filter(Objects::nonNull)
                    .forEach(update ->
                    {
                        if (update.callbackQuery() == null) {
                            Message message = update.message();
                            Long chatId = message.chat().id();
                            String text = message.text();
                            if ("/start".equals(text)) {
                                sendStartMessage(chatId);
                            }
                        } else {
                            callbackQueryCheck(update.callbackQuery());
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void callbackQueryCheck(CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        Long id = callbackQuery.from().id();
        if ("dog_shelter".equals(data)) {
            shelterMenu(id, ShelterType.DOGS_SHELTER);
        }
        if ("cat_shelter".equals(data)) {
            shelterMenu(id, ShelterType.CATS_SHELTER);
        }
        if ("shelter_info".equals(data)) {

        }
        if ("consultation".equals(data)) {

        }
        if ("pet_report".equals(data)) {

        }
        if ("volunteer".equals(data)) {

        }
    }

    private void sendStartMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Здравствуйте! Вас приветсвует сеть приютов для животных города Астаны. \n" +
                "На данном этапе вы будете взимодействовать с нашим ботом. Выберите к какому приюту вы бы хотели обратиться");
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для собак ").callbackData("dog_shelter"),
                new InlineKeyboardButton("Приют для кошек ").callbackData("cat_shelter")));
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    private void shelterMenu(Long chatId, ShelterType shelterType) {
        SendMessage sendMessage = null;
        if (shelterType.equals(ShelterType.DOGS_SHELTER)) {
            sendMessage = new SendMessage(chatId, "Это приют для собак " + dogsService.getShelter().getName());
        } else if (shelterType.equals(ShelterType.CATS_SHELTER)) {
            sendMessage = new SendMessage(chatId, "Это приют для кошек " + catsService.getShelter().getName());
        }
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Узнать информацию о приюте (этап 1)").callbackData(shelterType + "-shelter_info"),
                new InlineKeyboardButton("Как взять животное из приюта (этап 2)").callbackData(shelterType + "-consultation")
        ).addRow(new InlineKeyboardButton("Прислать отчет о питомце (этап 3) ").callbackData(shelterType + "-pet_report"),
                new InlineKeyboardButton("Позвать волонтера ").callbackData(shelterType + "-volunteer")));
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    private void part1(Long chatId, ShelterType shelterType) {

    }


}