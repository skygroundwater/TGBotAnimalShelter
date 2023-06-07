package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Animation;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Poll;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.GetChatResponse;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.listener.AnimalShelterBotListener;
import com.telegrambotanimalshelter.models.Shelter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/** Вспомогательный класс для listener {@link AnimalShelterBotListener}, содержащий методы для взаимодействия пользователя с кнопками, которые ему предоставляет бот. <br> <br>
 * Здесь находится весь функионал кнопок при взаимодействии пользователя с кнопкой <b> "Узнать информацию о приюте" ({@link AnimalShelterBotListener#shelterMenuMarkup(Shelter)}) </b>
 * @see AnimalShelterBotListener
 */
@Component
public class Part1 {

    private final TelegramBot telegramBot;

    private final Logger logger;

    public Part1(TelegramBot telegramBot,
                 Logger logger) {
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    /** Метод выводит сообщение и предлагает ряд кнопок с информацией по выбранному приюту. <br> <br>
     * <i> список выводимых кнопок в этом методе --> {@link Part1#part1Markup(Shelter)} </i>
     * @param chatId
     * @param shelter
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void part1(Long chatId, Shelter shelter) {
        sendResponse(sendMessagePart1(chatId, "Здравствуйте!", part1Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "О приюте" </b> ({@link Part1#part1Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void shelterInfo(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getDescription(), part1Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Адрес, время работы" </b> ({@link Part1#part1Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void shelterWorkingHours(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getWorkingHours(), part1Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Пропуск в приют" </b> ({@link Part1#part1Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void shelterPass(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getSecurityContacts(), part1Markup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Техника безопасностит" </b> ({@link Part1#part1Markup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
    public void shelterSafety(Long id, Shelter shelter) {
        sendResponse(sendMessagePart1(id, shelter.getSafetyPrecautions(), part1Markup(shelter)));
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

    private SendMessage sendMessagePart1(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        telegramBot.execute(new SendChatAction(id, ChatAction.typing));
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

    /**
     * Кнопки, выводимые для взаимодействия с пользователем, из метода {@link Part1#part1(Long, Shelter)}
     * @param shelter
     * @see Part1
     * @see Part2
     * @see AnimalShelterBotListener#callbackQueryCheck(CallbackQuery)
     */
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