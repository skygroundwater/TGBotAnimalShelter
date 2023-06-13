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

    private final ReportPart reportPart;

    private static boolean contactsRequest;

    private static boolean reportRequest;

    @Autowired
    public AnimalShelterBotListener(TelegramBot telegramBot,
                                    @Qualifier("catsServiceImpl") PetService<Cat> catsService,
                                    @Qualifier("dogsServiceImpl") PetService<Dog> dogsService,
                                    PetOwnersService petOwnersService,
                                    Logger logger, ReportPart reportPart) {
        this.telegramBot = telegramBot;
        this.catsService = catsService;
        this.dogsService = dogsService;
        this.petOwnersService = petOwnersService;
        this.logger = logger;
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
                            String preFix = text.split(" ")[0];
                            String info = text.substring(preFix.length() - 1);
                            if (contactsRequest) {
                                contactsRequestBlock(chatId, preFix, info);
                            }
                            if (reportRequest) {
                                reportFromPetOwnerBlock(chatId, preFix, message);
                            } else switch (text) {
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

        if ("_contacts".equals(data)) sendMessageToTakeName(id, dogShelter);
        if ("_report".equals(data)) startReportFromPetOwner(id, dogShelter);

        String preFix = data.split("_")[0];

        if (preFix.equals(dogShelterName)) {
            dogsService.callBackQueryServiceCheck(callbackQuery);
        } else if (preFix.equals(catShelterName)) {
            catsService.callBackQueryServiceCheck(callbackQuery);
        }
    }

    private void contactsRequestBlock(Long chatId, String prefix, String info) {
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

    private void startReportFromPetOwner(Long chatId, Shelter shelter) {
        reportRequest = true;
        sendMessage(chatId, "Итак, вы решили отправить-таки отчет по своему питомцу.\n" +
                "Следующим сообщением приложите его фотографии, предварительно прописав префикс *Фото: *." +
                "Чтобы прекратить процесс отправки отчета, воспользуйтесь командой /break");


    }

    private void reportFromPetOwnerBlock(Long chatId, String prefix, Message message) {

        switch (prefix) {
            case "Фото:" -> sendMessageToTakeDiet(chatId, message);
            case "Диета:" -> sendMessageToTakeCommonStatus(chatId, message);
            case "Состояние:" -> {
                sendConfirmMessage(chatId, "Спасибо Вам за ваш отчет. Если будет что-то не так - волонтёр отпишетися вам. Желаем удачи.");
                reportRequest = false;
            }
            case "/break" -> {
                sendStartMessage(chatId);
                reportRequest = false;
            }
            default -> sendWarningLetter(chatId);
        }

    }

    private void sendMessageToTakeDiet(Long chatId, Message message) {
        sendMessage(chatId, "Отлично. Теперь отправьте сообщешием повседневный рацион вашего животного. Префикс *Диета: *");

    }

    private void sendMessageToTakeCommonStatus(Long chatId, Message message) {
        sendMessage(chatId, "Мы уже близки к завершению. Поделитесь общим состоянием животного.\n" +
                " Как его самочувствие и процесс привыкания к новому месту? Префикс *Состояние: *");

    }

    private void sendMessageToTakeChanges(Long chatId, Message message) {
        sendMessage(chatId, "Последняя наша просьба - поделиться процессом изменения животного.\n" +
                "Как идет процесс восчпитания? Может быть, животное стало проявлять новые черты в своем поведении? Префикс *Изменения: *");

    }

    private void sendWarningLetter(Long chatId) {
        sendMessage(chatId, "«Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно,\n" +
                " как необходимо. Пожалуйста, подойди ответственнее к этому занятию. \n" +
                "В противном случае, волонтеры приюта будут обязаны \n" +
                "самолично проверять условия содержания животного».");


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