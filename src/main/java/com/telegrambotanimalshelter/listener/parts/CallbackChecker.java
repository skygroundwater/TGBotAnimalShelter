package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petservice.PetService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CallbackChecker {

    private final PetService<Cat> catsService;

    private final PetService<Dog> dogsService;

    private final MessageSender sender;

    private final VolunteerAndPetOwnerChat chat;

    private final ContactRequestBlock contactBlock;

    private final ReportPart reportPart;

    public CallbackChecker(@Qualifier("catsServiceImpl") PetService<Cat> catsService,
                           @Qualifier("dogsServiceImpl") PetService<Dog> dogsService,
                           MessageSender sender, VolunteerAndPetOwnerChat chat,
                           ContactRequestBlock contactBlock, ReportPart reportPart) {
        this.catsService = catsService;
        this.dogsService = dogsService;
        this.sender = sender;
        this.chat = chat;
        this.contactBlock = contactBlock;
        this.reportPart = reportPart;
    }

    public void callbackQueryCheck(CallbackQuery callbackQuery) {

        Shelter dogShelter = dogsService.getShelter();
        Shelter catShelter = catsService.getShelter();

        String dogShelterName = dogShelter.getName();
        String catShelterName = catShelter.getName();

        String data = callbackQuery.data();
        Long id = callbackQuery.from().id();

        if ("cat_shelter".equals(data)) shelterMenu(id, catShelter);
        if ("dog_shelter".equals(data)) shelterMenu(id, dogShelter);

        if ("back".equals(data)) sender.sendStartMessage(id);

        if ("_contacts".equals(data)) contactBlock.sendMessageToTakeName(id, dogShelter);
        if ("_report".equals(data)) reportPart.startReportFromPetOwner(id, dogShelter);

        if ("volunteer".equals(data)) {
            chat.startChat(id, "Здравствуйте. С вами хочет поговорить усыновитель. " + callbackQuery.from().firstName());
        }

        String preFix = data.split("_")[0];

        if (preFix.equals(dogShelterName)) {
            dogsService.callBackQueryServiceCheck(callbackQuery);
        } else if (preFix.equals(catShelterName)) {
            catsService.callBackQueryServiceCheck(callbackQuery);
        }
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