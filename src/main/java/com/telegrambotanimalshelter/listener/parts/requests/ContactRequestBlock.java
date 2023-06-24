package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

/**
 * Сущность, отвечающая за взаимодействие с пользователем
 * на этапе записи его контактных данных.
 * @param <A>
 * @param <R>
 */
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

    /**
     * Пройдя проверку на нахождение пользователя
     * в блоке запроса контактов, его сообщения попадают
     * в этот блок и проходят проверку на соответствие
     *
     * @param chatId  личный id поьзователя
     * @param message сообщение от пользователя
     */
    public void contactsRequestBlock(Long chatId, Message message) {
        String text = message.text();
        String preFix = text.split(" ")[0];
        String info = text.substring(preFix.length());
        switch (preFix) {
            case "Имя:" -> sendMessageToTakeSecondName(chatId, info);
            case "Фамилия:" -> sendMessageToTakeNumberOfPhone(chatId, info);
            case "Телефон:" -> saveContacts(chatId, info);
            case "/break" -> {
                forcedStopContactRequest(chatId);
            }
            default -> sender.sendMessage(chatId,
                    "Вы находитесь в блоке записи контактных данных. " +
                            "Чтобы выйти из него отправьте команду /break");
        }
    }

    /**
     * Сохраняем потенциального усыновителя в базу.
     *
     * @param update принимаем update от чата
     * @hidden В случае если поступала команда /start, то мы сразу
     * сохраняем потенциального усыновителя в базу данных.
     * Это нужно для того, чтобы дальше взаимодействовать
     * с пользователем вне зависимости от того, брал ли он
     * уже животных из приюта или нет.
     * @see PetOwnersService#savePotentialPetOwner(Update)
     */
    public void savePotentialPetOwner(Update update) {
        petOwnersService.savePotentialPetOwner(update);
    }

    /**
     * Проверяем находится ли пользователь в блоке
     * записи контактов в настоящий момент.
     *
     * @param chatId личный id пользователя
     * @return true or false
     * @hidden Если такого пользователя не существует в базе,
     * то возвращается false, если он есть, то проверяется
     * его boolean переменная contactRequest.
     */
    public boolean checkContactRequestStatus(Long chatId) {
        PetOwner petOwner = keeper.getPetOwners().get(chatId);
        if (petOwner == null) {
            return false;
        } else return petOwner.isContactRequest();
    }

    /**
     * Кладем в пользователя в базу данных с изменением
     * переменной contactRequest на true. И отправляем сообщение
     * с просьбой ввести имя.
     *
     * @param chatId личный id пользователя
     * @hidden К этому методу мы приходим после проверки
     * пользователя на то, является ли он на данный момент
     * в блоке записи контактов.
     */
    public void sendMessageToTakeName(Long chatId) {
        keeper.getPetOwners().put(chatId, petOwnersService.setPetOwnerContactRequest(chatId, true));
        sender.sendMessage(chatId, "Введите ваше имя после префикса *Имя: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    /**
     * В этом методе изменяем <b>имя</b> пользователя в кеше,
     * принимая его от пользователя. Далее отправляем
     * просьбу записать фамилию.
     *
     * @param chatId    личный id пользователя
     * @param firstName имя, отправленное пользователем
     */
    private void sendMessageToTakeSecondName(Long chatId, String firstName) {
        keeper.getPetOwners().get(chatId).setFirstName(firstName);
        sender.sendMessage(chatId, "Введите вашу Фамилию после префикса *Фамилия: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    /**
     * В этом метода изменияем <b>фамилию</b> пользователя в кеше,
     * принимая его от пользователя. Далее отправляем просьбу
     * записать номер телефона
     *
     * @param chatId   личный id пользователя
     * @param lastName фамилия, отправленная пользователем
     */
    private void sendMessageToTakeNumberOfPhone(Long chatId, String lastName) {
        keeper.getPetOwners().get(chatId).setLastName(lastName);
        sender.sendMessage(chatId, "Введите ваш номер телефона после префикса *Телефон: * \uD83E\uDEAA Не забудьте пробел после двоеточия.");
    }

    /**
     * В этом методе изменияем <b>номер телефона</b> пользователя в кеше,
     * принимая его от пользователя. И сохраняем его в базу данных.
     *
     * @param chatId      личный id пользователя
     * @param phoneNumber номер телефона, отправленный пользователем
     * @hidden Назначаем переменной contactRequest значение false.
     * И также сохраняем пользователя с новыми значениями переменных
     * в кеш.
     */
    private void saveContacts(Long chatId, String phoneNumber) {
        PetOwner petOwner = keeper.getPetOwners().get(chatId);
        petOwner.setPhoneNumber(phoneNumber);
        petOwner.setContactRequest(false);
        keeper.getPetOwners().put(petOwner.getId(), petOwner);
        petOwnersService.putPetOwner(petOwner);
        sender.sendMessage(chatId, "Ваши контакты успешно записаны. Можете продолжить работу с нашим ботом.");
        sender.sendStartMessage(chatId);
    }

    /**
     * В случае, если пользователь решил <b>преждевременно прервать</b>
     * запись контактов, отправив команду /break.
     *
     * @param chatId личный id пользователя
     * @hidden Назначаем переменной contactRequest значение false.
     * И также сохраняем пользователя с новыми значениями переменных
     * в кеш и базу данных.
     */
    private void forcedStopContactRequest(Long chatId) {
        sender.sendStartMessage(chatId);
        keeper.getPetOwners().put(chatId, petOwnersService.setPetOwnerContactRequest(chatId, false));
    }

}
