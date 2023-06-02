package com.telegrambotanimalshelter.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.service.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.service.petservice.PetService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final Logger logger;

    private final Part1 part1;

    @Autowired
    public AnimalShelterBotListener(TelegramBot telegramBot,
                                    @Qualifier("catsServiceImpl") PetService catsService,
                                    @Qualifier("dogsServiceImpl") PetService dogsService,
                                    PetOwnersService petOwnersService,
                                    Logger logger, Part1 part1) {
        this.telegramBot = telegramBot;
        this.catsService = catsService;
        this.dogsService = dogsService;
        this.petOwnersService = petOwnersService;
        this.logger = logger;
        this.part1 = part1;
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
                            switch (text) {
                                case "/start":
                                    sendStartMessage(chatId);
                                    break;
                                case "/break":

                                default:
                                    sendMessage(chatId, "Бот не может корректно прочесть ваше сообщение. Повторите снова");
                                    break;
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

        Shelter dogShelter = dogsService.getShelter();
        Shelter catShelter = catsService.getShelter();

        String dogShelterName = dogShelter.getName();
        String catShelterName = catShelter.getName();

        String data = callbackQuery.data();
        Long id = callbackQuery.from().id();

        if ("back".equals(data)) sendStartMessage(id);

        if ("cat_shelter".equals(data)) shelterMenu(id, catShelter);
        if ("dog_shelter".equals(data)) shelterMenu(id, dogShelter);

        if ((dogShelterName + "_shelter_info").equals(data)) part1.part1(id, dogShelter);
        if ((catShelterName + "_shelter_info").equals(data)) part1.part1(id, catShelter);

        if ((dogShelterName + "_info").equals(data)) part1.shelterInfo(id, dogShelter);
        if ((catShelterName + "_info").equals(data)) part1.shelterInfo(id, catShelter);

        if ((dogShelterName + "_hours").equals(data)) part1.shelterWorkingHours(id, dogShelter);
        if ((catShelterName + "_hours").equals(data)) part1.shelterWorkingHours(id, catShelter);

        if ((dogShelterName + "_pass").equals(data)) part1.shelterPass(id, dogShelter);
        if ((catShelterName + "_pass").equals(data)) part1.shelterPass(id, catShelter);

        if ((dogShelterName + "_safety").equals(data)) part1.shelterSafety(id, dogShelter);
        if ((catShelterName + "_safety").equals(data)) part1.shelterSafety(id, catShelter);

        if ((dogShelterName + "_contacts").equals(data)) part1.potentialOwnerContactsRequest(id, dogShelter);
        if ((catShelterName + "_contacts").equals(data)) part1.potentialOwnerContactsRequest(id, catShelter);


    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendResponse(sendMessage);
    }

    private void sendResponse(SendMessage sendMessage) {
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    private void sendStartMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Здравствуйте! Вас приветсвует сеть приютов для животных города Астаны. \n" +
                "На данном этапе вы будете взимодействовать с нашим ботом. Выберите к какому приюту вы бы хотели обратиться");
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для собак ").callbackData("dog_shelter"),
                new InlineKeyboardButton("Приют для кошек ").callbackData("cat_shelter")));
        sendResponse(sendMessage);
    }

    private void shelterMenu(Long chatId, Shelter shelter) {
        SendMessage sendMessage = null;
        String shelterName = shelter.getName();
        if (shelter.getShelterType().equals(ShelterType.DOGS_SHELTER)) {
            sendMessage = new SendMessage(chatId, "Это приют для собак " + shelterName);
            sendMessage.replyMarkup(shelterMenuMarkup(shelter));
        } else if (shelter.getShelterType().equals(ShelterType.CATS_SHELTER)) {
            sendMessage = new SendMessage(chatId, "Это приют для кошек " + shelterName);
            sendMessage.replyMarkup(shelterMenuMarkup(shelter));
        }
        sendResponse(sendMessage);
    }

    private InlineKeyboardMarkup shelterMenuMarkup(Shelter shelter) {
        String shelterName = shelter.getName();
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Узнать информацию о приюте").callbackData(shelterName + "_shelter_info"),
                new InlineKeyboardButton("Как взять животное из приюта").callbackData(shelterName + "_shelter_consultation")
        ).addRow(new InlineKeyboardButton("Прислать отчет о питомце").callbackData(shelterName + "_report"),
                new InlineKeyboardButton("Позвать волонтера").callbackData(shelterName + "_shelter_volunteer"));
    }
}