package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.listener.parts.*;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AnimalShelterBotListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final CallbackChecker checker;

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private final MessageSender sender;

    private final VolunteerAndPetOwnerChat chat;

    private final ContactRequestBlock contactBlock;

    private final Logger logger;

    private final ReportPart reportPart;

    @Autowired
    public AnimalShelterBotListener(TelegramBot telegramBot, CallbackChecker checker,
                                    PetOwnersService petOwnersService, VolunteerService volunteerService,
                                    MessageSender sender, VolunteerAndPetOwnerChat chat,
                                    ContactRequestBlock contactBlock, Logger logger, ReportPart reportPart) {
        this.telegramBot = telegramBot;
        this.checker = checker;
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.sender = sender;
        this.chat = chat;
        this.contactBlock = contactBlock;
        this.logger = logger;
        this.reportPart = reportPart;
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
                        if (update.callbackQuery() == null) {
                            Message message = update.message();
                            Long chatId = message.chat().id();
                            String text = message.text();
                            String preFix = text.split(" ")[0];
                            String info = text.substring(preFix.length() - 1);
                            switch (text) {
                                case "/start" -> {
                                    petOwnersService.savePotentialPetOwner(update);
                                    sender.sendStartMessage(chatId);
                                    return;
                                }
                            }
                            if (petOwnersService.checkVolunteerChatStatus(chatId)) {
                                chat.volunteerAndPetOwnerChat(chatId, null, text);
                            }
                            if (volunteerService.checkVolunteer(chatId)) {
                                chat.volunteerAndPetOwnerChat(null, chatId, text);
                            }
                            if (petOwnersService.checkContactRequestStatus(chatId)) {
                                contactBlock.contactsRequestBlock(chatId, preFix, info);
                            }
                            if (petOwnersService.checkReportRequestStatus(chatId)) {
                                reportPart.reportFromPetOwnerBlock(chatId, preFix, message);
                            }
                        } else {
                            checker.callbackQueryCheck(update.callbackQuery());
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}