package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

@Component
public class ContactRequestBlock {

    private final MessageSender sender;

    private final PetOwnersService petOwnersService;

    public ContactRequestBlock(MessageSender sender, PetOwnersService petOwnersService) {
        this.sender = sender;
        this.petOwnersService = petOwnersService;
    }

    public void contactsRequestBlock(Long chatId, String prefix, String info) {
        switch (prefix) {
            case "Имя:" -> sendMessageToTakeSecondName(chatId, info);
            case "Фамилия:" -> sendMessageToTakeNumberOfPhone(chatId, info);
            case "Телефон:" -> {
                sender.sendMessage(chatId, "Ваши контакты успешно записаны. Можете продолжить работу с нашим ботом.");
                sender.sendStartMessage(chatId);
                petOwnersService.setPetOwnerContactRequest(chatId, false);
            }
            case "/break" -> {
                sender.sendStartMessage(chatId);
                petOwnersService.setPetOwnerContactRequest(chatId, false);
            }
            default -> sender.sendMessage(chatId, "Вы находитесь в блоке записи контактных данных. Чтобы выйти из него отправьте команду /break");
        }
    }

    public void savePotentialPetOwner(Update update) {
        petOwnersService.savePotentialPetOwner(update);
    }

    public boolean checkContactRequestStatus(Long petOwnerId) {
        return petOwnersService.checkContactRequestStatus(petOwnerId);
    }

    public void sendMessageToTakeName(Long chatId, Shelter shelter) {
        petOwnersService.setPetOwnerContactRequest(chatId, true);
        sender.sendMessage(chatId, "Введите ваше имя после префикса *Имя: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    public void sendMessageToTakeSecondName(Long chatId, String name) {
        sender.sendMessage(chatId, "Введите вашу Фамилию после префикса *Фамилия: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    public void sendMessageToTakeNumberOfPhone(Long chatId, String secondName) {
        sender.sendMessage(chatId, "Введите ваш номер телефона после префикса *Телефон: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }
}
