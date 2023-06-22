package com.telegrambotanimalshelter.listener.parts.checker;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.listener.parts.BecomingPetOwnerPart;
import com.telegrambotanimalshelter.listener.parts.IntroductionPart;
import com.telegrambotanimalshelter.listener.parts.requests.ContactRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.utils.Constants;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CallbackChecker {

    private final ContactRequestBlock contactBlock;

    private final ReportRequestBlock reportRequestBlock;

    private final VolunteerAndPetOwnerChat chat;

    private final IntroductionPart introductionPart;

    private final BecomingPetOwnerPart becomingPart;

    private final Shelter dogShelter;

    private final Shelter catShelter;

    private final MessageSender sender;

    public CallbackChecker(ContactRequestBlock contactBlock, ReportRequestBlock reportRequestBlock,
                           VolunteerAndPetOwnerChat chat, IntroductionPart introductionPart,
                           BecomingPetOwnerPart becomingPart, MessageSender sender,
                           @Qualifier("dogShelter") Shelter dogShelter, @Qualifier("catShelter") Shelter catShelter) {
        this.contactBlock = contactBlock;
        this.reportRequestBlock = reportRequestBlock;
        this.chat = chat;
        this.introductionPart = introductionPart;
        this.becomingPart = becomingPart;
        this.dogShelter = dogShelter;
        this.catShelter = catShelter;
        this.sender = sender;
    }


    public void callbackQueryCheck(CallbackQuery callbackQuery) {

        String dogShelterName = dogShelter.getName();
        String catShelterName = catShelter.getName();

        String data = callbackQuery.data();
        Long id = callbackQuery.from().id();

        if ("cat_shelter".equals(data)) shelterMenu(id, catShelter);
        if ("dog_shelter".equals(data)) shelterMenu(id, dogShelter);

        if ("back".equals(data)) sender.sendStartMessage(id);

        if ("_contacts".equals(data)) contactBlock.sendMessageToTakeName(id, dogShelter);
        if ("_report".equals(data)) reportRequestBlock.choosePet(id, dogShelter);

        if ("volunteer".equals(data)) {
            chat.startChat(id, "Здравствуйте. С вами хочет поговорить усыновитель. " + callbackQuery.from().firstName());
        }

        String preFix = data.split("_")[0];

        if (preFix.equals(dogShelterName)) {
            callBackQueryConstantCheck(callbackQuery, dogShelter);
            if ("first_meeting".equals(callbackQuery.data())) {
                becomingPart.firstMeetingWithDog(callbackQuery.from().id(), dogShelter);
            }
        } else if (preFix.equals(catShelterName)) {
            callBackQueryConstantCheck(callbackQuery, catShelter);
        }
    }

    private void callBackQueryConstantCheck(CallbackQuery callbackQuery, Shelter shelter) {
        String shelterName = shelter.getName();
        String data = callbackQuery.data();
        Long id = callbackQuery.from().id();
        if ((shelterName + "_shelter_info").equals(data)) introductionPart.welcome(id, shelter);
        if ((shelterName + "_info").equals(data)) introductionPart.shelterInfo(id, shelter);
        if ((shelterName + "_hours").equals(data)) introductionPart.shelterWorkingHours(id, shelter);
        if ((shelterName + "_pass").equals(data)) introductionPart.shelterPass(id, shelter);
        if ((shelterName + "_safety").equals(data)) introductionPart.shelterSafety(id, shelter);
        if ((shelterName + "_shelter_consultation").equals(data)) becomingPart.welcome(id, shelter);
        if ((shelterName + "_acquaintance").equals(data)) becomingPart.acquaintanceWithPet(id, shelter);
        if ((shelterName + "_documents").equals(data)) becomingPart.documentsForPetOwner(id, shelter);
        if ((shelterName + "_transportation").equals(data)) becomingPart.transportation(id, shelter);
        if ((shelterName + "_little").equals(data)) becomingPart.homeForLittlePet(id, shelter);
        if ((shelterName + "_adult").equals(data)) becomingPart.homeForAdultPet(id, shelter);
        if ((shelterName + "_restricted").equals(data)) becomingPart.homeForRestrictedPet(id, shelter);
        if ((shelterName + "_reasons_for_refusal").equals(data)) becomingPart.reasonsForRefusal(id, shelter);
    }

    private void shelterMenu(Long chatId, Shelter shelter) {
        SendMessage sendMessage = null;
        String shelterName = shelter.getName();
        if (shelter.getShelterType().equals(ShelterType.DOGS_SHELTER)) {
            sendMessage = new SendMessage(chatId, "Это приют для собак " + shelterName);
            sendMessage.replyMarkup(shelterMenuMarkup(shelter));
        } else if (shelter.getShelterType().equals(ShelterType.CATS_SHELTER)) {
            sendMessage = new SendMessage(chatId, "Это приют для кошек " + shelterName);
            sendMessage.replyMarkup(shelterMenuMarkup(shelter));
        }
        sender.sendResponse(sendMessage);
    }

    private InlineKeyboardMarkup shelterMenuMarkup(Shelter shelter) {
        String shelterName = shelter.getName();
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Узнать информацию о приюте").callbackData(shelterName + "_shelter_info"))
                .addRow(new InlineKeyboardButton("Как взять животное из приюта").callbackData(shelterName + "_shelter_consultation"))
                .addRow(new InlineKeyboardButton("Прислать отчет о питомце").callbackData("_report"))
                .addRow(new InlineKeyboardButton("Обратиться к волонтеру").callbackData("volunteer"));
    }
}