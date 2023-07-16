package com.telegrambotanimalshelter.listener.parts.volunteerblock;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

@Component
public class VolunteerBlock<A extends Animal, R extends Report, I extends AppImage> {

    private final MessageSender<A> sender;

    private final CacheKeeper<A, R> keeper;

    private final AuthorizationBlock<A, R> authorizationBlock;

    private final CheckReportsBlock<A, R> checkReportsBlock;


    public VolunteerBlock(MessageSender<A> sender,
                          CacheKeeper<A, R> keeper,
                          AuthorizationBlock<A, R> authorizationBlock,
                          CheckReportsBlock<A, R> checkReportsBlock) {
        this.sender = sender;
        this.keeper = keeper;
        this.authorizationBlock = authorizationBlock;
        this.checkReportsBlock = checkReportsBlock;
    }

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    public void reportCheckingByVolunteerBlock(Long chatId, Message message) {
        if (authorizationBlock.checkAuthorization(chatId)) {
            authorizationBlock.authenticationBlock(chatId, message);
        } else {
            String text = message.text();
            switch (text) {
                case "Проверить отчеты" -> checkReportsBlock.startCheckingReports(chatId);
                case "Выйти из кабинета волонтёра" -> checkReportsBlock.getOut(chatId);
                case "Принять отчет" -> checkReportsBlock.acceptReport(chatId);
                case "Отклонить отчет" -> checkReportsBlock.rejectReport(chatId);
                case "Прервать проверку отчета" -> checkReportsBlock.forcedStopCheckReport(chatId);
                default -> sender.sendMessage(chatId,
                        "Если вы хотите выйти из своего кабинета, то нажмите кнопку <Прервать проверку отчета>");
            }
        }
    }

    private boolean checkVolunteer(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        if (volunteer != null) {
            if (volunteer.isFree()) {
                return true;
            }
            return volunteer.isInOffice();
        }
        return false;
    }

    public SendResponse authenticationByLoginAndPassword(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        if (checkVolunteer(chatId)) {
            if (volunteer != null) {
                return authorizationBlock.startAuthentication(volunteer);
            }
        }
        SendMessage sendMessage = new SendMessage(chatId, "Нет вы не волонтёр, но могли бы им стать");
        sendMessage.replyMarkup(new ReplyKeyboardMarkup(
                new KeyboardButton("Стать волонтёром"),
                new KeyboardButton("Не хочу быть волонтёром"))
                .oneTimeKeyboard(true));
        PetOwner registerPetOwnerLikeVolunteer
                = cache().getPetOwnersById().get(chatId);
        registerPetOwnerLikeVolunteer.setRegistering(true);
        cache().getPetOwnersById().put(chatId,
                keeper.getPetOwnersService()
                        .putPetOwner(registerPetOwnerLikeVolunteer));
        return sender.sendResponse(sendMessage);
    }

    public boolean checkOfficeStatusForVolunteer(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        if (volunteer != null) {
            return volunteer.isInOffice();
        } else return false;
    }
}
