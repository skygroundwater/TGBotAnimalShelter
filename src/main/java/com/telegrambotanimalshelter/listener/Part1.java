package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.models.Shelter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Part1 {

    private final TelegramBot telegramBot;

    private final Logger logger;

    public Part1(TelegramBot telegramBot,
                 Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    public void sendResponse(SendMessage sendMessage) {
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    public void part1(Long chatId, Shelter shelter) {
        sendResponse(sendMessagePart1(chatId, "Здравствуйте!", part1Markup(shelter)));
    }

    public void shelterInfo(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getDescription(), part1Markup(shelter)));
    }

    public void shelterWorkingHours(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getWorkingHours(), part1Markup(shelter)));
    }

    public void shelterPass(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getSecurityContacts(), part1Markup(shelter)));
    }

    public void shelterSafety(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getSafetyPrecautions(), part1Markup(shelter)));
    }

    public void potentialOwnerContactsRequest(Long id, Shelter shelter) {
        SendMessage sendMessage = new SendMessage(id, "Введите ваши контактные данные");
        sendResponse(sendMessage);
    }

    public SendMessage sendMessagePart1(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

    private InlineKeyboardMarkup part1Markup(Shelter shelter) {
        String shelterName = shelter.getName();
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("О приюте").callbackData(shelterName + "_info"),
                new InlineKeyboardButton("Адрес, время работы").callbackData(shelterName + "_hours")
        ).addRow(new InlineKeyboardButton("Пропуск в приют").callbackData(shelterName + "_pass"),
                        new InlineKeyboardButton("Техника безопасности").callbackData(shelterName + "_safety")
                ).addRow(new InlineKeyboardButton("Ваши контакты для связи").callbackData(shelterName + "_contacts"),
                        new InlineKeyboardButton("Волонтер").url("https://t.me/Anton_Ryabinin"))
                .addRow(new InlineKeyboardButton("Назад к выбору приюта").callbackData("back"));
    }
}