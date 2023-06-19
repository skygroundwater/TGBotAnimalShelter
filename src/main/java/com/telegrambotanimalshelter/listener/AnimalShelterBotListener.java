package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.listener.parts.checker.CallbackChecker;
import com.telegrambotanimalshelter.listener.parts.requests.ContactRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.utils.MessageSender;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AnimalShelterBotListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final VolunteerAndPetOwnerChat chat;

    private final ContactRequestBlock contactBlock;

    private final CallbackChecker checker;

    private final MessageSender sender;

    private final Logger logger;

    private final ReportRequestBlock reportRequestBlock;

    @Autowired
    public AnimalShelterBotListener(TelegramBot telegramBot, VolunteerAndPetOwnerChat chat,
                                    CallbackChecker checker, MessageSender sender, ReportRequestBlock reportRequestBlock,
                                    ContactRequestBlock contactBlock, Logger logger) {
        this.telegramBot = telegramBot;
        this.chat = chat;
        this.checker = checker;
        this.sender = sender;
        this.contactBlock = contactBlock;
        this.logger = logger;
        this.reportRequestBlock = reportRequestBlock;
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
                        if (update.callbackQuery() != null) {
                            checker.callbackQueryCheck(update.callbackQuery());
                        } else {
                            Message message = update.message();
                            Long chatId = message.chat().id();
                            String text = message.text();
                            String preFix = text.split(" ")[0];
                            String info = text.substring(preFix.length() - 1);
                            switch (text) {
                                case "/start" -> {
                                    contactBlock.savePotentialPetOwner(update);
                                    sender.sendStartMessage(chatId);
                                    return;
                                }
                            }
                            if (contactBlock.checkContactRequestStatus(chatId)) {
                                contactBlock.contactsRequestBlock(chatId, preFix, info);
                            }
                            if (reportRequestBlock.checkReportRequestStatus(chatId)) {
                                reportRequestBlock.reportFromPetOwnerBlock(chatId, preFix, message);
                            }
                            if (chat.checkPetOwnerChatStatus(chatId)) {
                                chat.continueChat(chatId, null, text);
                            }
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