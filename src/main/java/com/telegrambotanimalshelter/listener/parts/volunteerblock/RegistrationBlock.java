package com.telegrambotanimalshelter.listener.parts.volunteerblock;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.Role;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class RegistrationBlock<A extends Animal, R extends Report> {

    private final CacheKeeper<A, R> keeper;

    private final MessageSender<A> sender;

    private final PasswordEncoder encoder;

    public RegistrationBlock(CacheKeeper<A, R> keeper,
                             MessageSender<A> sender,
                             @Qualifier("encoder") PasswordEncoder encoder) {
        this.keeper = keeper;
        this.sender = sender;
        this.encoder = encoder;
    }

    @Setter
    @Getter
    static class RegistrationHelperForPetOwner {
        private boolean changedLogin;
        private boolean changedPassword;
        private String login;
        private String password;

        public RegistrationHelperForPetOwner() {
            changedPassword = false;
            changedLogin = false;
        }
    }

    ConcurrentMap<Long, RegistrationHelperForPetOwner> registeringPetOwnersId
            = new ConcurrentHashMap<>();

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    public boolean isPetOwnerInRegistrationBlock(Long chatId) {
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
        if (petOwner != null) {
            return petOwner.isRegistering();
        } else return false;
    }

    public SendResponse startRegistration(Long chatId) {
        registeringPetOwnersId.put(chatId, new RegistrationHelperForPetOwner());
        return sender.sendResponse(new SendMessage(chatId, "Следующим сообщением введите своё имя"));
    }

    public SendResponse forcedStopRegistration(Long chatId) {
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
        petOwner.setRegistering(false);
        keeper.getPetOwnersService().putPetOwner(petOwner);
        registeringPetOwnersId.remove(chatId);
        sender.sendResponse(new SendMessage(chatId, "Вы не решились стать волонтёром"));
        return sender.sendStartMessage(chatId);
    }

    public SendResponse registrationBlock(Long chatId, Message message) {
        String text = message.text();
        switch (text) {
            case "Стать волонтёром":
                return startRegistration(chatId);
            case "Не хочу быть волонтёром":
                return forcedStopRegistration(chatId);
        }
        RegistrationHelperForPetOwner helper
                = registeringPetOwnersId.get(chatId);
        if (!helper.isChangedLogin() && !helper.isChangedPassword()) {
            return login(chatId, text);
        }
        if (helper.isChangedLogin() && helper.getPassword() == null) {
            return password(chatId, text);
        }
        if (helper.isChangedLogin()) {
            return confirm(chatId, text);
        }
        return sender.sendResponse(new SendMessage(chatId, "какой-то косяк"));
    }


    public SendResponse login(Long chatId, String login) {
        RegistrationHelperForPetOwner helper = registeringPetOwnersId.get(chatId);
        helper.setLogin(login);
        helper.setChangedLogin(true);
        return sender.sendResponse(new SendMessage(chatId, "Следующим сообщением введите пароль")
                .replyMarkup(regMarkup()));
    }

    private ReplyKeyboardMarkup regMarkup() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton("Не хочу быть волонтёром"))
                .oneTimeKeyboard(true);
    }

    public SendResponse password(Long chatId, String password) {
        RegistrationHelperForPetOwner helper
                = registeringPetOwnersId.get(chatId);
        helper.setPassword(password);
        return sender.sendResponse(new SendMessage(
                chatId, "Введите повторно пароль для подтверждения")
                .replyMarkup(regMarkup()));
    }

    public SendResponse confirm(Long chatId, String passwordForConfirm) {
        RegistrationHelperForPetOwner helper
                = registeringPetOwnersId.get(chatId);
        String password = helper.getPassword();
        if (password.equals(passwordForConfirm)) {
            return stopRegistration(chatId, helper);
        }
        return sender.sendResponse(new SendMessage(chatId,
                "Пароль не совпадает, повторите ещё раз")
                .replyMarkup(regMarkup()));
    }

    private SendResponse stopRegistration(Long chatId,
                                          RegistrationHelperForPetOwner helper) {
        helper.setChangedPassword(true);
        PetOwner registeredPetOwnerLikeVolunteer = cache().getPetOwnersById().get(chatId);
        registeredPetOwnerLikeVolunteer.setRegistering(false);
        cache().getPetOwnersById().put(chatId, keeper.getPetOwnersService()
                .putPetOwner(registeredPetOwnerLikeVolunteer));
        Volunteer volunteer = new Volunteer();
        volunteer.setFirstName(registeredPetOwnerLikeVolunteer.getFirstName());
        volunteer.setLastName(registeredPetOwnerLikeVolunteer.getLastName());
        volunteer.setId(registeredPetOwnerLikeVolunteer.getId());
        volunteer.setUserName(helper.login);
        volunteer.setRole(Role.ROLE_VOLUNTEER);
        volunteer.setPassword(encoder.encode(helper.password));
        cache().getVolunteers().put(volunteer.getId(), volunteer);
        keeper.getVolunteersService().saveVolunteer(volunteer);
        sender.sendResponse(new SendMessage(chatId, "Вы успешно прошли регистрацию"));
        return sender.sendStartMessage(chatId);
    }
}