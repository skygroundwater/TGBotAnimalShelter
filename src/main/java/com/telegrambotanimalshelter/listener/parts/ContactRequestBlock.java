package com.telegrambotanimalshelter.listener.parts;

import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.sendMessage;

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
                sendConfirmMessage(chatId, "Ваши контакты успешно записаны. Можете продолжить работу с нашим ботом.");
                sender.sendStartMessage(chatId);
                petOwnersService.setPetOwnerContactRequest(chatId, false);
            }
            case "/break" -> {
                sender.sendStartMessage(chatId);
                petOwnersService.setPetOwnerContactRequest(chatId, false);
            }
            default -> sendMessage(sender, chatId, "Вы находитесь в блоке записи контактных данных. Чтобы выйти из него отправьте команду /break");
        }
    }

    public void sendConfirmMessage(Long chatId, String info) {
        sendMessage(sender, chatId, info);
    }

    public void sendMessageToTakeName(Long chatId, Shelter shelter) {
        petOwnersService.setPetOwnerContactRequest(chatId, true);
        sendMessage(sender, chatId, "Введите ваше имя после префикса *Имя: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    public void sendMessageToTakeSecondName(Long chatId, String name) {
        sendMessage(sender, chatId, "Введите вашу Фамилию после префикса *Фамилия: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    public void sendMessageToTakeNumberOfPhone(Long chatId, String secondName) {
        sendMessage(sender, chatId, "Введите ваш номер телефона после префикса *Телефон: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }
}
