package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
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

    public void welcome(Long chatId, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(chatId, "Здравствуйте!", introductionPartMarkup(shelter)));
    }

    public void shelterInfo(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getDescription(), introductionPartMarkup(shelter)));
    }

    public void shelterWorkingHours(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getWorkingHours(), introductionPartMarkup(shelter)));
    }

    public void shelterPass(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getSecurityContacts(), introductionPartMarkup(shelter)));
    }

    public void shelterSafety(Long id, Shelter shelter) {
        sender.sendResponse(introductionPartMessage(id, shelter.getSafetyPrecautions(), introductionPartMarkup(shelter)));
    }

    private SendMessage introductionPartMessage(Long id, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return new SendMessage(id, message).replyMarkup(inlineKeyboardMarkup);
    }

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