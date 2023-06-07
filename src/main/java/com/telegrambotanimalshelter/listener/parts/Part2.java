package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.listener.AnimalShelterBotListener;
import com.telegrambotanimalshelter.models.Shelter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.FIRST_MEETING_WITH_DOG;
import static com.telegrambotanimalshelter.utils.Constants.REASONS_FOR_REFUSAL;

/** Вспомогательный класс для listener {@link AnimalShelterBotListener}, содержащий методы для взаимодействия пользователя с кнопками, которые ему предоставляет бот. <br> <br>
 * Здесь находится весь функионал кнопок при взаимодействии пользователя с кнопкой <b> "Как взять животное из приюта" ({@link AnimalShelterBotListener#shelterMenuMarkup(Shelter)}) </b>
 * @see AnimalShelterBotListener
 */
@Component
public class Part2 {

    private final TelegramBot telegramBot;

    private final Logger logger;

    public Part2(TelegramBot telegramBot, Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    /** Метод выводит сообщение и предлагает ряд кнопок с информацией о том, что необходимо для опекунства животины. <br> <br>
     * <i> список выводимых кнопок в этом методе --> {@link Part2#part2Markup(Shelter)} </i>
     * @param id
     * @param shelter
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void part2(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, "Здравствуйте! Это 2-ой этап", part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Знакомство с животным" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void acquaintanceWithPet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getAcquaintance(), part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Документы от опекуна" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void documentsForPetOwner(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getContractDocuments(), part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Транспортировка собаки" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void transportation(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getTransportation(), part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Дом для щенка/котенка" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void homeForLittlePet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getHomeForLittle(), part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Дом для взрослого щенка/котенка" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void homeForAdultPet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getHomeForAdult(), part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Дом для собаки/котов с ограничениями" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void homeForRestrictedPet(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, shelter.getHomeForRestricted(), part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Причины отказа приюта отдать собаку/кота" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void reasonsForRefusal(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, REASONS_FOR_REFUSAL, part2Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Первое общение с собакой/кошкой" </b> ({@link Part2#part2Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void firstMeetingWithDog(Long id, Shelter shelter) {
        sendResponse(sendMessagePart2(id, FIRST_MEETING_WITH_DOG, part2Markup(shelter)));
    }

    /**
     * Отправка сформированного ответа пользователю. <br>
     * Если формирование ответа прошло не успешно, бросается ошибка {@link Logger#error(String)}
     * @param sendMessage
     */
    private void sendResponse(SendMessage sendMessage) {
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.message());
        }
    }

    private SendMessage sendMessagePart2(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

    /**
     * Кнопки, выводимые для взаимодействия с пользователем, из метода {@link Part2#part2(Long, Shelter)}
     * @param shelter
     * @see Part1
     * @see Part2
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
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
                            .callbackData(shelterName + "_contacts"))
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
                            .callbackData(shelterName + "_contacts"))
                    .addRow(new InlineKeyboardButton("Назад к выбору приюта")
                            .callbackData("back"));
        }
        return part2Keyboard;
    }
}
