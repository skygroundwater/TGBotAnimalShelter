package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Part2 {

    private final TelegramBot telegramBot;

    private final Logger logger;

    public Part2(TelegramBot telegramBot, Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    private void sendResponse(SendMessage sendMessage) {
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    private SendMessage sendMessagePart2(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

    private InlineKeyboardMarkup part2Markup(Shelter shelter) {
        String shelterName = shelter.getName();
        InlineKeyboardMarkup part2Keyboard = null;
        if (shelter.getShelterType().equals(ShelterType.DOGS_SHELTER)) {
            part2Keyboard = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("Знакомство с собакой").callbackData(shelterName + "_acquaintance"),
                    new InlineKeyboardButton("Документы от опекуна").callbackData(shelterName + "_documents")
            ).addRow(new InlineKeyboardButton("Транспортировка собаки").callbackData(shelterName + "_transportation"),
                            new InlineKeyboardButton("Дом для щенка").callbackData(shelterName + "_home_for_little")
                    ).addRow(new InlineKeyboardButton("Дом для взрослой собаки").callbackData(shelterName + "_home_for_adult"),
                            new InlineKeyboardButton("Дом для собаки с ограничениями").callbackData("_home_for_restricted"))
                    .addRow(new InlineKeyboardButton("Первое общение с собакой").callbackData("first_meeting"),
                            new InlineKeyboardButton("Список проверенных кинологов").callbackData("proven_cynologist"))
                    .addRow(new InlineKeyboardButton("Причины отказа приюта отдать собаку").callbackData("reasons_for_refusal"))
                    .addRow(new InlineKeyboardButton("Обратиться к волонтеру").url("https://t.me/Anton_Ryabinin"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта").callbackData("back"));
        } else if (shelter.getShelterType().equals(ShelterType.CATS_SHELTER)) {
            part2Keyboard = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("Знакомство с животным").callbackData(shelterName + "_acquaintance"),
                    new InlineKeyboardButton("Документы от опекуна").callbackData(shelterName + "_documents")
            ).addRow(new InlineKeyboardButton("Транспортировка животного").callbackData(shelterName + "_transportation"),
                            new InlineKeyboardButton("Дом для щенка").callbackData(shelterName + "_home_for_little")
                    ).addRow(new InlineKeyboardButton("Дом для взрослой кошки").callbackData(shelterName + "_home_for_adult"),
                            new InlineKeyboardButton("Дом для кошки с ограничениями").callbackData("_home_for_restricted"))
                    .addRow(new InlineKeyboardButton("Причины отказа приюта отдать кошку").callbackData("reasons_for_refusal"))
                    .addRow(new InlineKeyboardButton("Обратиться к волонтеру").url("https://t.me/Anton_Ryabinin"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта").callbackData("back"));
        }
        return part2Keyboard;
    }
}
