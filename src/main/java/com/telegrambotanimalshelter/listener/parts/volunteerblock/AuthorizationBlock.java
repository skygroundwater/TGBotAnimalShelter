package com.telegrambotanimalshelter.listener.parts.volunteerblock;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AuthorizationBlock<A extends Animal, R extends Report> {

    private final MessageSender<A> sender;

    private final CacheKeeper<A, R> keeper;

    private final PasswordEncoder encoder;

    public AuthorizationBlock(MessageSender<A> sender,
                              CacheKeeper<A, R> keeper,
                              @Qualifier("encoder") PasswordEncoder encoder) {
        this.sender = sender;
        this.keeper = keeper;
        this.encoder = encoder;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class AuthorizationModel {

        private boolean isAuthenticating;

        private boolean loginChecked;

        private String validLogin;

        private String validEncodedPassword;

        public AuthorizationModel(boolean isAuthenticating,
                                  String login, String validEncodedPassword) {
            this.isAuthenticating = isAuthenticating;
            this.validLogin = login;
            this.validEncodedPassword = validEncodedPassword;
            loginChecked = false;
        }
    }

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    private static ConcurrentMap<Long, AuthorizationModel> authorizationMap
            = new ConcurrentHashMap<>();


    public boolean checkAuthorization(Long chatId) {
        AuthorizationModel authorizationModel = authorizationMap.get(chatId);
        if (authorizationModel != null) {
            return authorizationModel.isAuthenticating;
        } else return false;
    }

    public SendResponse startAuthentication(Volunteer volunteer) {
        volunteer.setInOffice(true);
        volunteer.setFree(false);
        cache().getVolunteers().put(volunteer.getId(),
                keeper.getVolunteersService().putVolunteer(volunteer));
        authorizationMap.put(volunteer.getId(), new AuthorizationModel(true,
                volunteer.getUserName(), volunteer.getPassword()));
        return sender.sendResponse(new SendMessage(
                volunteer.getId(), "Следующим сообщением введите ваш логин"));
    }

    public SendResponse authenticationBlock(Long chatId, Message message) {
        AuthorizationModel authorizationModel = authorizationMap.get(chatId);
        if (authorizationModel != null) {
            if (message.text().equals("Остановить аутентификацию")) {
                return forcedStopAuthorization(chatId);
            }
            if (authorizationModel.isAuthenticating() && !authorizationModel.isLoginChecked()) {
                return sendMessageToTakePassword(chatId, message);
            }
            if (authorizationModel.isLoginChecked()) {
                return checkPassword(chatId, message);
            }
        }
        return sender.sendResponse(new SendMessage(chatId, "Что-то пошло не так"));
    }


    private SendResponse forcedStopAuthorization(Long chatId) {
        authorizationMap.remove(chatId);
        volunteerWantsToGetOutFromOffice(chatId);
        sender.sendResponse(new SendMessage(chatId, "ВЫ покинули блок аутентификации"));
        return sender.sendStartMessage(chatId);
    }


    private SendResponse checkPassword(Long chatId, Message message) {
        AuthorizationModel authorizationModel = authorizationMap.get(chatId);
        String password = message.text();
        if (encoder.matches(password, authorizationModel.getValidEncodedPassword())) {
            authorizationMap.remove(chatId);
            startWorkWithVolunteer(chatId);
            return sender.sendResponse(
                    new SendMessage(
                            chatId, "Вы успешно прошли авторизацию"));
        } else return sender.sendResponse(new SendMessage(
                chatId, "Вы ввели невалидный пароль. Попробуйте снова")
                .replyMarkup(stopAuthorizationMarkup()));
    }

    private ReplyKeyboardMarkup stopAuthorizationMarkup() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton("Остановить аутентификацию"))
                .oneTimeKeyboard(true);
    }

    private SendResponse sendMessageToTakePassword(Long chatId, Message message) {
        AuthorizationModel authorizationModel = authorizationMap.get(chatId);
        String login = message.text();
        if (login.equals(authorizationModel.getValidLogin())) {
            authorizationModel.setLoginChecked(true);
            authorizationMap.put(chatId, authorizationModel);
            return sender.sendResponse(
                    new SendMessage(chatId, "Логин прошел проверку! Следующим шагом введите ваш пароль")
                            .replyMarkup(stopAuthorizationMarkup()));
        } else {
            return sender.sendResponse(
                    new SendMessage(chatId, "Логин не прошел проверку, повторите еще раз")
                            .replyMarkup(stopAuthorizationMarkup()));
        }
    }

    public Volunteer volunteerWantsToGetOutFromOffice(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        volunteer.setCheckingReports(false);
        volunteer.setInOffice(false);
        volunteer.setFree(true);
        cache().getVolunteers().put(volunteer.getId(), volunteer);
        keeper.getVolunteersService().putVolunteer(volunteer);
        return volunteer;
    }

    public Volunteer startWorkWithVolunteer(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        sender.sendResponse(new SendMessage(chatId,
                "Здравствуйте, " + volunteer.getFirstName() + ". Спасибо, что помогаете нам, мы очень это ценим.")
                .replyMarkup(volunteerKeyboardInOffice()));
        return volunteer;
    }

    private ReplyKeyboardMarkup volunteerKeyboardInOffice() {
        return new ReplyKeyboardMarkup("Проверить отчеты")
                .addRow("Выйти из кабинета волонтёра")
                .oneTimeKeyboard(true);
    }

}
