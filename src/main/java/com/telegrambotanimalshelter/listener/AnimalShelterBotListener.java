package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.listener.parts.checker.CallbackChecker;
import com.telegrambotanimalshelter.listener.parts.requests.ChoosePetForPotentialOwnerBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ContactRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.listener.parts.volunteerblock.VolunteerBlock;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AnimalShelterBotListener<A extends Animal, R extends Report, I extends AppImage> implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final VolunteerAndPetOwnerChat<A, R> chat;

    private final ContactRequestBlock<A, R> contactBlock;

    private final VolunteerBlock<A, R, I> volunteerBlock;

    private final CallbackChecker<A, R, I> checker;

    private final MessageSender<A> sender;

    private final Logger logger;

    private final ReportRequestBlock<A, R, I> reportRequestBlock;

    private final ChoosePetForPotentialOwnerBlock<A, R> choosePetForOwnerBlock;

    @Autowired
    public AnimalShelterBotListener(TelegramBot telegramBot,
                                    VolunteerAndPetOwnerChat<A, R> chat,
                                    VolunteerBlock<A, R, I> volunteerBlock,
                                    CallbackChecker<A, R, I> checker,
                                    MessageSender<A> sender,
                                    ReportRequestBlock<A, R, I> reportRequestBlock,
                                    ContactRequestBlock<A, R> contactBlock,
                                    Logger logger, ChoosePetForPotentialOwnerBlock<A, R> choosePetForOwnerBlock) {
        this.telegramBot = telegramBot;
        this.chat = chat;
        this.volunteerBlock = volunteerBlock;
        this.checker = checker;
        this.sender = sender;
        this.contactBlock = contactBlock;
        this.logger = logger;
        this.reportRequestBlock = reportRequestBlock;
        this.choosePetForOwnerBlock = choosePetForOwnerBlock;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.stream()
                    .filter(Objects::nonNull)
                    .forEach(update ->
                    {
                        /*
                           Производится проверка на наличие скрытых данных в обновлении от кнопок
                         */
                        if (update.callbackQuery() != null) {
                            /*
                          Если есть, то, используя отдельную сущность CallBackChecker осуществляем проверку этих данных
                             */
                            checker.callbackQueryCheck(update.callbackQuery());
                        } else {
                            /*
                            Если таких данных нет, то отправляемся проверять сообщение
                             */
                            Message message = update.message();
                            Long chatId = message.chat().id();
                            String text = message.text();
                            /*
                            Сначала проверяем текст сообщения на команды
                             */
                            if ("/start".equals(text)) {
                                contactBlock.savePotentialPetOwner(update);
                                sender.sendStartMessage(chatId);
                            }
                            /*
                            Если команды не поступало, то приступаем к
                            проверкам статуса пользователя на данном этапе
                             */

                            /*
                            Сначала проверяем на статус записи контактов пользователя
                             */
                            if (contactBlock.checkContactRequestStatus(chatId)) {
                                contactBlock.contactsRequestBlock(chatId, message);
                            }

                            /*
                            Далее проверяем на статус записи отчета о питомце
                             */
                            if (reportRequestBlock.checkReportRequestStatus(chatId)) {
                                reportRequestBlock.reportFromPetOwnerBlock(chatId, message);
                            }
                            /*
                            Далее проверяем, если это волонтёр, то сначала на то,
                            находится ли он в статусе проверяющего отчеты
                             */
                            if (volunteerBlock.checkOfficeStatusForVolunteer(chatId)) {
                                volunteerBlock.reportCheckingByVolunteerBlock(chatId, message);
                            }
                            /*
                            Далее проверяем не выбирает ли пользователь животное в данный момент
                             */
                            if (choosePetForOwnerBlock.checkIfPetOwnerChoosingPet(chatId)) {
                                choosePetForOwnerBlock.choosingPetForPetOwnerBlock(chatId, message);
                            }
                            /*
                            Далее проверяем на статус того, является ли пользователь
                            на данный момент в чате с волонтёром
                             */
                            if (chat.checkPetOwnerChatStatus(chatId)) {
                                chat.continueChat(chatId, null, text);
                            }
                            /*
                            Здесь мы проверяем является ли пользователь самим волонтером
                             */
                            if (chat.checkVolunteer(chatId)) {
                                chat.continueChat(null, chatId, text);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}