package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.listener.parts.Part1;
import com.telegrambotanimalshelter.listener.parts.Part2;
import com.telegrambotanimalshelter.listener.parts.ReportPart;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petservice.PetService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class AnimalShelterBotListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final PetService<Cat> catsService;

    private final PetService<Dog> dogsService;

    private final PetOwnersService petOwnersService;

    private final Logger logger;

    private final Part1 part1;

    private final Part2 part2;

    private final ReportPart reportPart;

    private static boolean contactsRequest;

    private static boolean reportRequest;

    @Autowired
    public AnimalShelterBotListener(TelegramBot telegramBot,
                                    @Qualifier("catsServiceImpl") PetService<Cat> catsService,
                                    @Qualifier("dogsServiceImpl") PetService<Dog> dogsService,
                                    PetOwnersService petOwnersService,
                                    Logger logger, Part1 part1, Part2 part2, ReportPart reportPart) {
        this.telegramBot = telegramBot;
        this.catsService = catsService;
        this.dogsService = dogsService;
        this.petOwnersService = petOwnersService;
        this.logger = logger;
        this.part1 = part1;
        this.part2 = part2;
        this.reportPart = reportPart;
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
                            if (contactsRequest) {
                                String preFix = text.split(" ")[0];
                                String info = text.substring(preFix.length() - 1);
                                contactsRequest(chatId, preFix, info);
                            } else if(reportRequest){

                            }
                                switch (text) {
                                    case "/start" -> {
                                        savePotentialPetOwner(update);
                                        sendStartMessage(chatId);
                                    }
                                    default ->
                                            sendMessage(chatId, "Бот не может корректно прочесть ваше сообщение. Повторите снова");
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

        if ((dogShelterName + "_contacts").equals(data)) sendMessageToTakeName(id, dogShelter);
        if ((catShelterName + "_contacts").equals(data)) sendMessageToTakeName(id, catShelter);

        if ((dogShelterName + "_shelter_consultation").equals(data)) part2.part2(id, dogShelter);
        if ((catShelterName + "_shelter_consultation").equals(data)) part2.part2(id, catShelter);

        if ((dogShelterName + "_acquaintance").equals(data)) part2.acquaintanceWithPet(id, dogShelter);
        if ((catShelterName + "_acquaintance").equals(data)) part2.acquaintanceWithPet(id, catShelter);

        if ((dogShelterName + "_documents").equals(data)) part2.documentsForPetOwner(id, dogShelter);
        if ((catShelterName + "_documents").equals(data)) part2.documentsForPetOwner(id, catShelter);

        if ((dogShelterName + "_transportation").equals(data)) part2.transportation(id, dogShelter);
        if ((catShelterName + "_transportation").equals(data)) part2.transportation(id, catShelter);

        if ((dogShelterName + "_little").equals(data)) part2.homeForLittlePet(id, dogShelter);
        if ((catShelterName + "_little").equals(data)) part2.homeForLittlePet(id, catShelter);

        if ((dogShelterName + "_adult").equals(data)) part2.homeForAdultPet(id, dogShelter);
        if ((catShelterName + "_adult").equals(data)) part2.homeForAdultPet(id, catShelter);

        if ((dogShelterName + "_restricted").equals(data)) part2.homeForRestrictedPet(id, dogShelter);
        if ((catShelterName + "_restricted").equals(data)) part2.homeForRestrictedPet(id, catShelter);

        if ((dogShelterName + "_reasons_for_refusal").equals(data)) part2.reasonsForRefusal(id, dogShelter);
        if ((catShelterName + "_reasons_for_refusal").equals(data)) part2.reasonsForRefusal(id, catShelter);

        if ((dogShelterName + "_report").equals(data)) reportFromPetOwner(id, dogShelter);
        if ((catShelterName + "_report").equals(data)) reportFromPetOwner(id, catShelter);

        if ("first_meeting".equals(data)) part2.firstMeetingWithDog(id, dogShelter);
    }

    private void contactsRequest(Long chatId, String prefix, String info) {
        switch (prefix) {
            case "Имя:" -> sendMessageToTakeSecondName(chatId, info);
            case "Фамилия:" -> sendMessageToTakeNumberOfPhone(chatId, info);
            case "Телефон:" -> {
                sendConfirmMessage(chatId, "Ваши контакты успешно записаны. Можете продолжить работу с нашим ботом.");
                sendStartMessage(chatId);
                contactsRequest = false;
            }
            case "/break" -> {
                sendStartMessage(chatId);
                contactsRequest = false;
            }
            default ->
                    sendMessage(chatId, "Вы находитесь в блоке записи контактных данных. Чтобы выйти из него отправьте команду /break");
        }
    }

    private void reportFromPetOwner(Long chatId, Shelter shelter) {
        reportRequest = true;
        sendMessage(chatId, "Вы попали в блок отправки отчета волонтеру");

    }


    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.parseMode(ParseMode.Markdown);
        sendResponse(sendMessage);
    }

    private void sendResponse(SendMessage sendMessage) {
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    private void savePotentialPetOwner(Update update) {
        Message message = update.message();
        Chat chat = message.chat();
        try {
            petOwnersService.findPetOwnerById(chat.id());
        } catch (NotFoundInDataBaseException e) {
            petOwnersService.savePetOwnerToDB(new PetOwner(chat.id(), chat.firstName(), chat.lastName(),
                    chat.username(), LocalDateTime.now(), false));
        }
    }

    private void sendStartMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId,
                "Здравствуйте! Вас приветсвует сеть приютов для животных города Астаны. \n" +
                        "На данном этапе вы будете взимодействовать с нашим ботом. Выберите к какому приюту вы бы хотели обратиться");
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для собак ").callbackData("dog_shelter"),
                new InlineKeyboardButton("Приют для кошек ").callbackData("cat_shelter")));
        sendResponse(sendMessage);
    }

    private void sendConfirmMessage(Long chatId, String info) {
        sendMessage(chatId, info);
    }

    //блок запроса контактов от пользователя
    private void sendMessageToTakeName(Long chatId, Shelter shelter) {
        contactsRequest = true;
        sendMessage(chatId, "Введите ваше имя после префикса *Имя: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    private void sendMessageToTakeSecondName(Long chatId, String name) {

        sendMessage(chatId, "Введите вашу Фамилию после префикса *Фамилия: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    private void sendMessageToTakeNumberOfPhone(Long chatId, String secondName) {
        sendMessage(chatId, "Введите ваш номер телефона после префикса *Телефон: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }
    //блок запроса контактов от пользователя


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
                new InlineKeyboardButton("Узнать информацию о приюте").callbackData(shelterName + "_shelter_info"))
                .addRow(new InlineKeyboardButton("Как взять животное из приюта").callbackData(shelterName + "_shelter_consultation"))
                .addRow(new InlineKeyboardButton("Прислать отчет о питомце").callbackData(shelterName + "_report"))
                .addRow(new InlineKeyboardButton("Позвать волонтера").callbackData(shelterName + "_shelter_volunteer"));
    }
}