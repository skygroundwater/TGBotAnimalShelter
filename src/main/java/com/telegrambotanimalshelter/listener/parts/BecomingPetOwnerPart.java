package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.listener.AnimalShelterBotListener;
import com.telegrambotanimalshelter.listener.parts.checker.CallbackChecker;
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

    /**
     * Метод выводит сообщение и предлагает ряд кнопок с информацией о том, что необходимо для опекунства животины. <br> <br>
     * <i> список выводимых кнопок в этом методе --> {@link BecomingPetOwnerPart#becomingPartMarkup(Shelter)}  </i>
     *
     * @param chatId
     * @param shelter
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse welcome(Long chatId, Shelter shelter) {
        String shelterName = shelter.getName();
        if (shelterName.equals(dogShelterName)) {
            return sender.sendResponse(becomingPart(chatId, "Здравствуйте! Это приют " +
                    dogShelterName + ". Здесь вы можете ознакомиться с приютом", becomingPartMarkup(shelter)));
        } else if (shelterName.equals(catShelterName)) {
            return sender.sendResponse(becomingPart(chatId, "Здравствуйте! Это приют " +
                    catShelterName + ". Здесь вы можете ознакомиться с приютом", becomingPartMarkup(shelter)));
        } else throw new RuntimeException();
    }

    /**
     * При нажатии пользователя на кнопку <b> "Знакомство с животным" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse acquaintanceWithPet(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, shelter.getAcquaintance(), becomingPartMarkup(shelter)));
    }

    /**
     * При нажатии пользователя на кнопку <b> "Документы от опекуна" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse documentsForPetOwner(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, shelter.getContractDocuments(), becomingPartMarkup(shelter)));
    }

    /**
     * При нажатии пользователя на кнопку <b> "Транспортировка собаки" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse transportation(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, shelter.getTransportation(), becomingPartMarkup(shelter)));
    }

    /**
     * При нажатии пользователя на кнопку <b> "Дом для щенка/котенка" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse homeForLittlePet(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, shelter.getHomeForLittle(), becomingPartMarkup(shelter)));
    }

    /**
     * При нажатии пользователя на кнопку <b> "Дом для взрослого щенка/котенка" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse homeForAdultPet(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, shelter.getHomeForAdult(), becomingPartMarkup(shelter)));
    }

    /**
     * При нажатии пользователя на кнопку <b> "Дом для собаки/котов с ограничениями" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse homeForRestrictedPet(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, shelter.getHomeForRestricted(), becomingPartMarkup(shelter)));
    }

    /**
     * При нажатии пользователя на кнопку <b> "Причины отказа приюта отдать собаку/кота" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse reasonsForRefusal(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, REASONS_FOR_REFUSAL, becomingPartMarkup(shelter)));
    }

    /**
     * При нажатии пользователя на кнопку <b> "Первое общение с собакой/кошкой" </b> ({@link BecomingPetOwnerPart#welcome(Long, Shelter)}) вызывается данный метод.
     *
     * @param chatId
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public SendResponse firstMeetingWithDog(Long chatId, Shelter shelter) {
        return sender.sendResponse(becomingPart(chatId, FIRST_MEETING_WITH_DOG, becomingPartMarkup(shelter)));
    }

    private SendMessage becomingPart(Long chatId, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(chatId, message).replyMarkup(inlineKeyboardMarkup);
    }

    /**
     * Кнопки, выводимые для взаимодействия с пользователем, из метода {@link BecomingPetOwnerPart#welcome(Long, Shelter)}
     *
     * @param shelter
     * @see MessageSender
     * @see BecomingPetOwnerPart
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
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
