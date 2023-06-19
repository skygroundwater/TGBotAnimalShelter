package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.FIRST_MEETING_WITH_DOG;
import static com.telegrambotanimalshelter.utils.Constants.REASONS_FOR_REFUSAL;

@Component
public class BecomingPetOwnerPart {

    private final MessageSender sender;

    public BecomingPetOwnerPart(MessageSender sender) {
        this.sender = sender;
    }

    public void part2(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, "Здравствуйте! Это 2-ой этап", becomingPartMarkup(shelter)));
    }

    public void acquaintanceWithPet(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, shelter.getAcquaintance(), becomingPartMarkup(shelter)));
    }

    public void documentsForPetOwner(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, shelter.getContractDocuments(), becomingPartMarkup(shelter)));
    }

    public void transportation(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, shelter.getTransportation(), becomingPartMarkup(shelter)));
    }

    public void homeForLittlePet(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, shelter.getHomeForLittle(), becomingPartMarkup(shelter)));
    }

    public void homeForAdultPet(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, shelter.getHomeForAdult(), becomingPartMarkup(shelter)));
    }

    public void homeForRestrictedPet(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, shelter.getHomeForRestricted(), becomingPartMarkup(shelter)));
    }

    public void reasonsForRefusal(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, REASONS_FOR_REFUSAL, becomingPartMarkup(shelter)));
    }

    public void firstMeetingWithDog(Long id, Shelter shelter) {
        sender.sendResponse(becomingPart(id, FIRST_MEETING_WITH_DOG, becomingPartMarkup(shelter)));
    }

    private SendMessage becomingPart(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

    private InlineKeyboardMarkup becomingPartMarkup(Shelter shelter) {
        String shelterName = shelter.getName();
        InlineKeyboardMarkup keyboard = null;
        if (shelter.getShelterType().equals(ShelterType.DOGS_SHELTER)) {
            keyboard = new InlineKeyboardMarkup(
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
                            .callbackData("_volunteer"))
                    .addRow(new InlineKeyboardButton("Запишем ваши контактные данные")
                            .callbackData("_contacts"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта")
                            .callbackData("back"));
        } else if (shelter.getShelterType().equals(ShelterType.CATS_SHELTER)) {
            keyboard = new InlineKeyboardMarkup(
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
                            .callbackData("volunteer"))
                    .addRow(new InlineKeyboardButton("Запишем ваши контактные данные")
                            .callbackData("_contacts"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта")
                            .callbackData("back"));
        }
        return keyboard;
    }
}
