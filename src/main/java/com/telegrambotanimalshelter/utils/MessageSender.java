package com.telegrambotanimalshelter.utils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.listener.AnimalShelterBotListener;
import com.telegrambotanimalshelter.listener.parts.checker.CallbackChecker;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.timer.ReportNotificationTimer;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.START_MESSAGE;

@Component
public class MessageSender<A extends Animal> {

    private final TelegramBot telegramBot;

    private final Logger logger;


    public MessageSender(TelegramBot telegramBot, Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    /**
     * Принимает <b><u>chatId</b></u> пользователя и выводит приветственное сообщение при отправке пользователем команды <b><u>/start</b></u>. <br> <br>
     * Также бот выводит кнопки выбора одного из приютов: для кошек ({@link ShelterType#CATS_SHELTER}) или собак ({@link ShelterType#DOGS_SHELTER}) <br> <br>
     * <i> следующий этап взаимодействия с ботом --> {@link CallbackChecker#shelterMenu(Long, Shelter)}  </i>
     *
     * @param chatId not null.
     */
    public SendResponse sendStartMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, START_MESSAGE);
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для собак ").callbackData("dog_shelter"),
                new InlineKeyboardButton("Приют для кошек ").callbackData("cat_shelter"))
                .addRow(new InlineKeyboardButton("Вы волонтёр").callbackData("i_am_volunteer")));
        return sendResponse(sendMessage);
    }

    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.parseMode(ParseMode.Markdown);
        telegramBot.execute(new SendChatAction(chatId, ChatAction.typing));
        sendResponse(sendMessage);
    }

    public void sendChatMessage(Long chatId, String msg) {
        SendMessage sendMessage = new SendMessage(chatId, msg);
        sendMessage.replyMarkup(new ReplyKeyboardMarkup(new KeyboardButton("Прекратить чат")));
        telegramBot.execute(new SendChatAction(chatId, ChatAction.typing));
        sendResponse(sendMessage);
    }

    public void sendResponse(SendPhoto sendPhoto) {
        SendResponse sendResponse = telegramBot.execute(sendPhoto);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    /**
     * Отправка сформированного ответа пользователю. <br>
     * Если формирование ответа прошло не успешно, бросается ошибка {@link Logger#error(String)}
     *
     * @param sendMessage
     */
    public SendResponse sendResponse(SendMessage sendMessage) {
        sendMessage.parseMode(ParseMode.Markdown);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
        return sendResponse;
    }

    /**
     * При срабатывании таймера ({@link ReportNotificationTimer#notificationToSendReport()} / {@link ReportNotificationTimer#checkLastReportFromPet(Long, Animal)})
     * бот отправляет пользователю сообщения о том, что ему необходимо отправить отчет по питомцу
     *
     * @param chatId
     * @param petNames
     * @see CallbackChecker#callbackQueryCheck(CallbackQuery)
     */
    public void sendMessageToSendReport(Long chatId, String petNames) {
        String text = "Пришлите отчет по вашим подопечным: *" + petNames
                + "*\n Ждём информации сегодня до конца дня";

        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.parseMode(ParseMode.Markdown);

        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Отправить отчет")
                        .callbackData("_report")
        ));
        sendResponse(sendMessage);
    }
}
