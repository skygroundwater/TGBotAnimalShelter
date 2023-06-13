package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.FIRST_MEETING_WITH_DOG;
import static com.telegrambotanimalshelter.utils.Constants.REASONS_FOR_REFUSAL;

@Component
public class Part2 {

    private final TelegramBot telegramBot;

    private final Logger logger;

    public Part2(TelegramBot telegramBot, Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    public void part2(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, "Здравствуйте! Это 2-ой этап", part2Markup(shelter)));
    }

    public void acquaintanceWithPet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getAcquaintance(), part2Markup(shelter)));
    }

    public void documentsForPetOwner(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getContractDocuments(), part2Markup(shelter)));
    }

    public void transportation(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getTransportation(), part2Markup(shelter)));
    }

    public void homeForLittlePet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getHomeForLittle(), part2Markup(shelter)));
    }

    public void homeForAdultPet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getHomeForAdult(), part2Markup(shelter)));
    }

    public void homeForRestrictedPet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getHomeForRestricted(), part2Markup(shelter)));
    }

    public void reasonsForRefusal(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, REASONS_FOR_REFUSAL, part2Markup(shelter)));
    }

    public void firstMeetingWithDog(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, FIRST_MEETING_WITH_DOG, part2Markup(shelter)));
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
                    new InlineKeyboardButton("Знакомство с собакой")
                            .callbackData(shelterName + "_acquaintance"),
                    new InlineKeyboardButton("Документы от опекуна")
                            .callbackData(shelterName + "_documents")
            ).addRow(new InlineKeyboardButton("Транспортировка собаки")
                                    .callbackData(shelterName + "_transportation"),
                            new InlineKeyboardButton("Дом для щенка")
                                    .callbackData(shelterName + "_little")
                    ).addRow(new InlineKeyboardButton("Дом для взрослой собаки")
                                    .callbackData(shelterName + "_adult"),
                            new InlineKeyboardButton("Дом для собаки с ограничениями")
                                    .callbackData(shelterName + "_restricted")
                    ).addRow(new InlineKeyboardButton("Первое общение с собакой")
                                    .callbackData("first_meeting"),
                            new InlineKeyboardButton("ЦКФ города Астана")
                                    .url("https://zoosfera.kz/club#contact"))
                    .addRow(new InlineKeyboardButton("Причины отказа приюта отдать собаку")
                            .callbackData(shelterName + "_reasons_for_refusal"))
                    .addRow(new InlineKeyboardButton("Обратиться к волонтеру")
                            .url("https://t.me/Anton_Ryabinin"))
                    .addRow(new InlineKeyboardButton("Запишем ваши контактные данные")
                            .callbackData("_contacts"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта")
                            .callbackData("back"));
        } else if (shelter.getShelterType().equals(ShelterType.CATS_SHELTER)) {
            part2Keyboard = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("Знакомство с кошкой")
                            .callbackData(shelterName + "_acquaintance"),
                    new InlineKeyboardButton("Документы от опекуна")
                            .callbackData(shelterName + "_documents")
            ).addRow(new InlineKeyboardButton("Транспортировка кошки")
                                    .callbackData(shelterName + "_transportation"),
                            new InlineKeyboardButton("Дом для котёнка")
                                    .callbackData(shelterName + "_little")
                    ).addRow(new InlineKeyboardButton("Дом для взрослой кошки")
                                    .callbackData(shelterName + "_adult"),
                            new InlineKeyboardButton("Дом для кошки с ограничениями")
                                    .callbackData(shelterName + "_restricted"))
                    .addRow(new InlineKeyboardButton("Причины отказа приюта отдать кошку")
                            .callbackData(shelterName + "_reasons_for_refusal"))
                    .addRow(new InlineKeyboardButton("Обратиться к волонтеру")
                            .url("https://t.me/Anton_Ryabinin"))
                    .addRow(new InlineKeyboardButton("Запишем ваши контактные данные")
                            .callbackData("_contacts"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта")
                            .callbackData("back"));
        }
        return part2Keyboard;
    }
}
