package com.telegrambotanimalshelter.utils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MessageSender<A extends Animal> {

    private final TelegramBot telegramBot;

    private final Logger logger;

    public MessageSender(TelegramBot telegramBot, Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    public void sendStartMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId,
                "Здравствуйте! Вас приветсвует сеть приютов для животных города Астаны. \n" +
                        "На данном этапе вы будете взимодействовать с нашим ботом. Выберите к какому приюту вы бы хотели обратиться");
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для собак ").callbackData("dog_shelter"),
                new InlineKeyboardButton("Приют для кошек ").callbackData("cat_shelter")));
        sendResponse(sendMessage);
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

    public void choosePetMessage(Long chatId, A animal){
        if(animal instanceof Dog) {
            SendPhoto sendPhoto = new SendPhoto(chatId, new byte[]{});
            sendPhoto.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Выбрать " + animal.getNickName()).callbackData(chatId + " " + ((Dog) animal).getId())));
            sendResponse(sendPhoto);
        } else if (animal instanceof Cat){
            SendPhoto sendPhoto = new SendPhoto(chatId, new byte[]{});
            sendPhoto.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Выбрать " + animal.getNickName()).callbackData(chatId + " " + ((Cat) animal).getId())));
            sendResponse(sendPhoto);
        }
    }

    public void sendResponse(SendPhoto sendPhoto) {
        SendResponse sendResponse = telegramBot.execute(sendPhoto);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    public void sendResponse(SendMessage sendMessage) {
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }
}
