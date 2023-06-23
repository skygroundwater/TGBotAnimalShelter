package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

@Component
public class ContactRequestBlock<A extends Animal, R extends Report> {

    private final MessageSender<A> sender;

    private final CacheKeeper<A, R> keeper;

    private final PetOwnersService petOwnersService;


    public ContactRequestBlock(MessageSender<A> sender, PetOwnersService petOwnersService, CacheKeeper<A, R> keeper) {
        this.sender = sender;
        this.petOwnersService = petOwnersService;
        this.keeper = keeper;
    }

    public void contactsRequestBlock(Long chatId, Message message) {
        String text = message.text();
        String preFix = text.split(" ")[0];
        String info = text.substring(preFix.length());
        switch (preFix) {
            case "Имя:" -> sendMessageToTakeSecondName(chatId, info);
            case "Фамилия:" -> sendMessageToTakeNumberOfPhone(chatId, info);
            case "Телефон:" -> saveContacts(chatId, info);

            case "/break" -> {
                sender.sendStartMessage(chatId);
                keeper.getPetOwners().put(chatId, petOwnersService.setPetOwnerContactRequest(chatId, false));
            }
            default ->
                    sender.sendMessage(chatId, "Вы находитесь в блоке записи контактных данных. Чтобы выйти из него отправьте команду /break");
        }
    }

    public void savePotentialPetOwner(Update update) {
        petOwnersService.savePotentialPetOwner(update);
    }

    public boolean checkContactRequestStatus(Long petOwnerId) {
        try {
            return keeper.getPetOwners().get(petOwnerId).isContactRequest();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void sendMessageToTakeName(Long chatId, Shelter shelter) {
        keeper.getPetOwners().put(chatId, petOwnersService.setPetOwnerContactRequest(chatId, true));
        sender.sendMessage(chatId, "Введите ваше имя после префикса *Имя: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    private void sendMessageToTakeSecondName(Long chatId, String firstName) {
        keeper.getPetOwners().get(chatId).setFirstName(firstName);
        sender.sendMessage(chatId, "Введите вашу Фамилию после префикса *Фамилия: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    private void sendMessageToTakeNumberOfPhone(Long chatId, String lastName) {
        keeper.getPetOwners().get(chatId).setLastName(lastName);
        sender.sendMessage(chatId, "Введите ваш номер телефона после префикса *Телефон: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    private void saveContacts(Long chatId, String phoneNumber) {
        PetOwner petOwner = keeper.getPetOwners().get(chatId);
        petOwner.setPhoneNumber(phoneNumber);
        petOwner.setContactRequest(false);
        keeper.getPetOwners().put(petOwner.getId(), petOwner);
        petOwnersService.putPetOwner(petOwner);
        sender.sendMessage(chatId, "Ваши контакты успешно записаны. Можете продолжить работу с нашим ботом.");
        sender.sendStartMessage(chatId);
    }

}
