package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.models.Shelter;
import org.springframework.stereotype.Component;

@Component
public class IntroductionPart {

    private final MessageSender sender;

    public IntroductionPart(MessageSender sender) {
        this.sender = sender;
    }

    public void welcome(Long chatId, Shelter shelter) {
        sendResponse(introductionPart(chatId, "Здравствуйте!", part1Markup(shelter)));
    }

    public void shelterInfo(Long id, Shelter shelter) {
        sendResponse(introductionPart(id, shelter.getDescription(), part1Markup(shelter)));
    }

    public void shelterWorkingHours(Long id, Shelter shelter) {
        sendResponse(introductionPart(id, shelter.getWorkingHours(), part1Markup(shelter)));
    }

    public void shelterPass(Long id, Shelter shelter) {
        sendResponse(introductionPart(id, shelter.getSecurityContacts(), part1Markup(shelter)));
    }

    public void shelterSafety(Long id, Shelter shelter) {
        sendResponse(introductionPart(id, shelter.getSafetyPrecautions(), part1Markup(shelter)));
    }

    private void sendResponse(SendMessage sendMessage) {
        sender.sendResponse(sendMessage);
    }

    private SendMessage introductionPart(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

    private InlineKeyboardMarkup part1Markup(Shelter shelter) {
        String shelterName = shelter.getName();
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("О приюте").callbackData(shelterName + "_info"),
                new InlineKeyboardButton("Адрес, время работы").callbackData(shelterName + "_hours")
        ).addRow(new InlineKeyboardButton("Пропуск в приют").callbackData(shelterName + "_pass"),
                        new InlineKeyboardButton("Техника безопасности").callbackData(shelterName + "_safety")
                ).addRow(new InlineKeyboardButton("Ваши контакты для связи").callbackData("_contacts"),
                        new InlineKeyboardButton("Обратиться к волонтеру").callbackData("volunteer"))
                .addRow(new InlineKeyboardButton("Назад к выбору приюта").callbackData("back"));
    }
}