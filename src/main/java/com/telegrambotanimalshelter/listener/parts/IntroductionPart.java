package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.listener.AnimalShelterBotListener;
import com.telegrambotanimalshelter.listener.parts.checker.CallbackChecker;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

/**
 * Сущность, отвечающая за этап консультации с новым пользователем.
 * На данном этапе бот должен давать вводную информацию о приюте:
 * где он находится, как и когда работает, какие правила пропуска на
 * территорию приюта, правила нахождения внутри и общения с животным.
 * Функционал приюта для кошек и для собак идентичный, но информация
 * внутри будет разной, так как приюты находятся в разном месте
 * и у них разные ограничения и правила нахождения с животными.
 */
@Component
public class IntroductionPart {

    private final MessageSender<Animal> sender;

    public IntroductionPart(MessageSender<Animal> sender) {
        this.sender = sender;
    }

    /** Выводит сообщение, и предлагает ряд кнопок с информацией по выбранному приюту. <br> <br>
     * <i> список выводимых кнопок в этом методе --> {@link IntroductionPart#introductionPartMarkup(Shelter)} </i>
     * @param chatId
     * @param shelter
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public void welcome(Long chatId, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(chatId, "Здравствуйте!", introductionPartMarkup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "О приюте" </b> ({@link IntroductionPart#introductionPartMarkup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public void shelterInfo(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getDescription(), introductionPartMarkup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Адрес, время работы" </b> ({@link IntroductionPart#introductionPartMarkup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public void shelterWorkingHours(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getWorkingHours(), introductionPartMarkup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Пропуск в приют" </b> ({@link IntroductionPart#introductionPartMarkup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public void shelterPass(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getSecurityContacts(), introductionPartMarkup(shelter)));
    }

    /** При нажатии пользователя на кнопку <b> "Техника безопасности" </b> ({@link IntroductionPart#introductionPartMarkup(Shelter)}) вызывается данный метод.
     * @param id
     * @param shelter
     * @return Выводит информацию и список тех же кнопок
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    public void shelterSafety(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getSafetyPrecautions(), introductionPartMarkup(shelter)));
    }

    private SendMessage introductionPartMessage(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

    /**
     * Кнопки, выводимые для взаимодействия с пользователем, из метода {@link IntroductionPart#welcome(Long, Shelter)}
     * @param shelter
     * @see MessageSender
     * @see BecomingPetOwnerPart
     * @see CallbackChecker#callBackQueryConstantCheck(CallbackQuery, Shelter)
     */
    private InlineKeyboardMarkup introductionPartMarkup(Shelter shelter) {
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