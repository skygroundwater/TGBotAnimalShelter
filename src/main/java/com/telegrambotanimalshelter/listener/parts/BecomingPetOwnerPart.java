package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.*;

/**
 * Сущность, отвечающая за этап консультации с потенциальным хозяином животного из приюта.
 * *На данном этапе бот помогает потенциальным усыновителям животного из приюта разобраться
 * с бюрократическими (оформление договора) и бытовыми (как подготовиться к жизни с животным)
 * вопросами.
 */
@Component
public class BecomingPetOwnerPart {

    private final MessageSender<Animal> sender;

    public BecomingPetOwnerPart(MessageSender<Animal> sender) {
        this.sender = sender;
    }

    public void welcome(Long chatId, Shelter shelter) {
        String shelterName = shelter.getName();
        if (shelterName.equals(dogShelterName)) {
            sender.sendResponse(becomingPart(chatId, "Здравствуйте! Это приют " +
                    dogShelterName + ". Здесь вы можете ознакомиться с приютом", becomingPartMarkup(shelter)));
        } else if (shelterName.equals(catShelterName)) {
            sender.sendResponse(becomingPart(chatId, "Здравствуйте! Это приют " +
                    catShelterName + ". Здесь вы можете ознакомиться с приютом", becomingPartMarkup(shelter)));
        }
    }

    public void acquaintanceWithPet(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, shelter.getAcquaintance(), becomingPartMarkup(shelter)));
    }

    public void documentsForPetOwner(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, shelter.getContractDocuments(), becomingPartMarkup(shelter)));
    }

    public void transportation(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, shelter.getTransportation(), becomingPartMarkup(shelter)));
    }

    public void homeForLittlePet(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, shelter.getHomeForLittle(), becomingPartMarkup(shelter)));
    }

    public void homeForAdultPet(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, shelter.getHomeForAdult(), becomingPartMarkup(shelter)));
    }

    public void homeForRestrictedPet(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, shelter.getHomeForRestricted(), becomingPartMarkup(shelter)));
    }

    public void reasonsForRefusal(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, REASONS_FOR_REFUSAL, becomingPartMarkup(shelter)));
    }

    public void firstMeetingWithDog(Long chatId, Shelter shelter) {
        sender.sendResponse(becomingPart(chatId, FIRST_MEETING_WITH_DOG, becomingPartMarkup(shelter)));
    }

    private SendMessage becomingPart(Long chatId, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(chatId, message).replyMarkup(inlineKeyboardMarkup);
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
                                    .callbackData(shelterName + "_first_meeting"),
                            new InlineKeyboardButton("ЦКФ города Астана")
                                    .url("https://zoosfera.kz/club#contact"))
                    .addRow(new InlineKeyboardButton("Причины отказа приюта отдать собаку")
                            .callbackData(shelterName + "_reasons_for_refusal"))
                    .addRow(new InlineKeyboardButton("Обратиться к волонтеру")
                            .callbackData("_volunteer"))
                    .addRow(new InlineKeyboardButton("Запишем ваши контактные данные")
                            .callbackData("_contacts"))
                    .addRow(new InlineKeyboardButton("Взять собаку из приюта")
                            .callbackData("_get_dog"))
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
                    .addRow(new InlineKeyboardButton("Взять кошку из приюта")
                            .callbackData("_get_cat"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта")
                            .callbackData("back"));
        }
        return keyboard;
    }
}
