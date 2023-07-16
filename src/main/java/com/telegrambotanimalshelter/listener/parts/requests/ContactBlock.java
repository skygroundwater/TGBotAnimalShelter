package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.utils.MessageSender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Сущность, отвечающая за взаимодействие с пользователем
 * на этапе записи его контактных данных.
 *
 * @param <A>
 * @param <R>
 */
@Component
public class ContactBlock<A extends Animal, R extends Report> {

    private final MessageSender<A> sender;

    private final CacheKeeper<A, R> keeper;

    public ContactBlock(MessageSender<A> sender,
                        CacheKeeper<A, R> keeper) {
        this.sender = sender;
        this.keeper = keeper;
    }

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    private static final ConcurrentMap<Long, ContactRequestHelper> helpers = new ConcurrentHashMap<>();

    @Getter
    @Setter
    static class ContactRequestHelper {

        private String firstName;

        private String lastName;

        private String phoneNumber;

        public ContactRequestHelper() {
        }
    }

    /**
     * Пройдя проверку на нахождение пользователя
     * в блоке запроса контактов, его сообщения попадают
     * в этот блок и проходят проверку на соответствие
     *
     * @param chatId  личный id пользователя
     * @param message сообщение от пользователя
     */
    public SendResponse contactsRequestBlock(Long chatId, Message message) {
        String info = message.text();
        ContactRequestHelper helper = helpers.get(chatId);
        if (!info.isEmpty()) {
            if (info.equals("/break")) {
                return forcedStopContactRequest(chatId);
            }
        } else return sender.sendResponse(new SendMessage(chatId, "Вы прислали не валидное сообщение"));
        if (helper.getFirstName() == null && helper.getLastName() == null && helper.getPhoneNumber() == null) {
            return sendMessageToTakeSecondName(chatId, info);
        } else if (helper.getFirstName() != null && helper.getLastName() == null && helper.getPhoneNumber() == null) {
            return sendMessageToTakeNumberOfPhone(chatId, info);
        } else if (helper.getFirstName() != null && helper.getLastName() != null && helper.getPhoneNumber() == null) {
            return saveContacts(chatId, info);
        } else return sender.sendResponse(new SendMessage(chatId, "Произошла ошибка"));
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
    public PetOwner savePotentialPetOwner(Update update) {
        PetOwner savedPetOwner = keeper.getPetOwnersService().savePotentialPetOwner(update);
        return cache().getPetOwnersById().put(savedPetOwner.getId(), savedPetOwner);
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
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
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
    public SendResponse sendMessageToTakeName(Long chatId) {
        helpers.put(chatId, new ContactRequestHelper());
        cache().getPetOwnersById().put(chatId, keeper.getPetOwnersService()
                .setPetOwnerContactRequest(chatId, true));
        return sender.sendResponse(new SendMessage(chatId, "Введите следующим сообщением ваше имя \uD83E\uDEAA"));
    }

    /**
     * В этом методе изменяем <b>имя</b> пользователя в кеше,
     * принимая его от пользователя. Далее отправляем
     * просьбу записать фамилию.
     *
     * @param chatId    личный id пользователя
     * @param firstName имя, отправленное пользователем
     */
    private SendResponse sendMessageToTakeSecondName(Long chatId, String firstName) {
        helpers.forEach((aLong, contactRequestHelper) -> {
            if (aLong.equals(chatId)) {
                contactRequestHelper.setFirstName(firstName);
            }
        });
        return sender.sendResponse(new SendMessage(chatId, "Введите следующим сообщением вашу Фамилию \uD83E\uDEAA"));
    }

    /**
     * В этом метода изменяем <b>фамилию</b> пользователя в кеше,
     * принимая его от пользователя. Далее отправляем просьбу
     * записать номер телефона
     *
     * @param chatId   личный id пользователя
     * @param lastName фамилия, отправленная пользователем
     */
    private SendResponse sendMessageToTakeNumberOfPhone(Long chatId, String lastName) {
        helpers.forEach((aLong, contactRequestHelper) -> {
            if (aLong.equals(chatId)) {
                contactRequestHelper.setLastName(lastName);
            }
        });
        return sender.sendResponse(new SendMessage(chatId, "Введите следующим сообщением ваш номер телефона \uD83E\uDEAA"));
    }

    /**
     * В этом методе изменяем <b>номер телефона</b> пользователя в кеше,
     * принимая его от пользователя. И сохраняем его в базу данных.
     *
     * @param chatId      личный id пользователя
     * @param phoneNumber номер телефона, отправленный пользователем
     * @hidden Назначаем переменной contactRequest значение false.
     * И также сохраняем пользователя с новыми значениями переменных
     * в кеш.
     */
    private SendResponse saveContacts(Long chatId, String phoneNumber) {
        ContactRequestHelper helper = helpers.get(chatId);
        helpers.remove(chatId);
        cache().getPetOwnersById().forEach(
                (aLong, petOwner) -> {
                    if (aLong.equals(chatId)) {
                        petOwner.setFirstName(helper.firstName);
                        petOwner.setLastName(helper.lastName);
                        petOwner.setPhoneNumber(phoneNumber);
                        petOwner.setContactRequest(false);
                        keeper.getPetOwnersService().putPetOwner(petOwner);
                    }
                });
        cache().getVolunteers().forEach((aLong, volunteer) -> {
            if (aLong.equals(chatId)) {
                volunteer.setFirstName(helper.firstName);
                volunteer.setLastName(helper.lastName);
                keeper.getVolunteersService().putVolunteer(volunteer);
            }
        });
        sender.sendMessage(chatId, "Ваши контакты успешно записаны. Можете продолжить работу с нашим ботом.");
        return sender.sendStartMessage(chatId);
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
    private SendResponse forcedStopContactRequest(Long chatId) {
        cache().getPetOwnersById()
                .put(chatId, keeper.getPetOwnersService()
                        .setPetOwnerContactRequest(
                                chatId, false));
        return sender.sendStartMessage(chatId);
    }
}