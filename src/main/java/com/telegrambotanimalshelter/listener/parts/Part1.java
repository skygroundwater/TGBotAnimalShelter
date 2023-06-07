package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Animation;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Poll;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.GetChatResponse;
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

    private void sendResponse(SendMessage sendMessage) {
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    private SendMessage sendMessagePart1(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        telegramBot.execute(new SendChatAction(id, ChatAction.typing));
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