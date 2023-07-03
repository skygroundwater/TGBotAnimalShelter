package com.telegrambotanimalshelter.listener.parts.checker;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.listener.parts.BecomingPetOwnerPart;
import com.telegrambotanimalshelter.listener.parts.IntroductionPart;
import com.telegrambotanimalshelter.listener.parts.requests.ChoosePetForPotentialOwnerBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ContactRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.listener.parts.volunteerblock.VolunteerBlock;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.catShelterName;
import static com.telegrambotanimalshelter.utils.Constants.dogShelterName;

/**
 * Сущность, отвечающая за проверку скрытых данных от кнопок.
 *
 * @param <A>
 * @param <R>
 * @param <I>
 */
@Component
public class CallbackChecker<A extends Animal, R extends Report, I extends AppImage> {

    private int choosePetMenu = 0;
    private Animal animal;
    private final ContactRequestBlock<A, R> contactBlock;

    private final ReportRequestBlock<A, R, I> reportRequestBlock;

    private final VolunteerAndPetOwnerChat<A, R> chat;

    private final IntroductionPart introductionPart;

    private final BecomingPetOwnerPart becomingPart;

    private final Shelter dogShelter;

    private final Shelter catShelter;

    private final MessageSender<A> sender;

    private final VolunteerBlock volunteerBlock;

    private final ChoosePetForPotentialOwnerBlock choosePetForPotentialOwnerBlock;

    public CallbackChecker(ContactRequestBlock<A, R> contactBlock,
                           ReportRequestBlock<A, R, I> reportRequestBlock,
                           VolunteerAndPetOwnerChat<A, R> chat,
                           IntroductionPart introductionPart,
                           BecomingPetOwnerPart becomingPart,
                           MessageSender<A> sender,
                           @Qualifier("dogShelter") Shelter dogShelter,
                           @Qualifier("catShelter") Shelter catShelter,
                           VolunteerBlock volunteerBlock,
                           ChoosePetForPotentialOwnerBlock choosePetForPotentialOwnerBlock) {
        this.contactBlock = contactBlock;
        this.reportRequestBlock = reportRequestBlock;
        this.chat = chat;
        this.introductionPart = introductionPart;
        this.becomingPart = becomingPart;
        this.dogShelter = dogShelter;
        this.catShelter = catShelter;
        this.sender = sender;
        this.volunteerBlock = volunteerBlock;
        this.choosePetForPotentialOwnerBlock = choosePetForPotentialOwnerBlock;
    }


    public void callbackQueryCheck(CallbackQuery callbackQuery) {

        String data = callbackQuery.data();
        Long chatId = callbackQuery.from().id();

        if ("cat_shelter".equals(data)) shelterMenu(chatId, catShelter);
        if ("dog_shelter".equals(data)) shelterMenu(chatId, dogShelter);

        if ("back".equals(data)) {
            choosePetMenu = 0;
            sender.sendStartMessage(chatId);
        }

        if ("_contacts".equals(data)) contactBlock.sendMessageToTakeName(chatId);
        if ("_report".equals(data)) reportRequestBlock.startReportFromPetOwner(chatId);
        if ("i_am_volunteer".equals(data)) volunteerBlock.startWorkWithVolunteer(chatId);

        if ("_get_cat".equals(data)) {
            choosePetForPotentialOwnerBlock.sendNotShelteredAnimals(data, chatId);
            choosePetMenu(catShelter);
        }
        if ("_get_dog".equals(data)) {
            choosePetForPotentialOwnerBlock.sendNotShelteredAnimals(data, chatId);
            choosePetMenu(dogShelter);
        }
        if ("_animal_info".equals(data)) choosePetForPotentialOwnerBlock.getAnimalInfo(animal, chatId);
        if ("_animal_photo".equals(data)) choosePetForPotentialOwnerBlock.getPetPhotoFromShelter(animal, chatId);
        if ("_animal_approve".equals(data)) choosePetForPotentialOwnerBlock.getPetFromShelter(animal, chatId);

        if ("volunteer".equals(data)) {
            chat.startChat(chatId, "Здравствуйте. С вами хочет поговорить усыновитель. " + callbackQuery.from().firstName());
        }
        if ((dogShelterName + "_first_meeting").equals(data)) {
            becomingPart.firstMeetingWithDog(callbackQuery.from().id(), dogShelter);
        }

        String preFix = data.split("_")[0];

        if (preFix.equals(dogShelterName)) {
            callBackQueryConstantCheck(callbackQuery, dogShelter);
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

    private void choosePetMenu(Shelter shelter) {
        if (shelter.getShelterType().equals(ShelterType.DOGS_SHELTER)) {
            choosePetMenu = 1;
        } else if (shelter.getShelterType().equals(ShelterType.CATS_SHELTER)) {
            choosePetMenu = 2;
        }
    }

    private InlineKeyboardMarkup getAnimalInfoMarkup() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Посмотреть информацию о будущем питомце")
                        .callbackData("_animal_info"))
                .addRow(new InlineKeyboardButton("Посмотреть фото будущего питомца").callbackData("_animal_photo"))
                .addRow(new InlineKeyboardButton("Приютить животное").callbackData("_animal_approve"))
                .addRow(new InlineKeyboardButton("Назад к выбору приюта").callbackData("back"));
    }

    public void inputNameFromUser(Long chatId, String text) {
        SendMessage sendMessage;

        switch (choosePetMenu) {
            case (1) -> {
                Dog dog = choosePetForPotentialOwnerBlock.getDogByNameFromUserRequest(text, chatId); //todo запрос по имени
                animal = dog;
                sendMessage = new SendMessage(chatId, dog.getNickName());
                sendMessage.replyMarkup(getAnimalInfoMarkup());
                sender.sendResponse(sendMessage);
            }

            case (2) -> {
                Cat cat = choosePetForPotentialOwnerBlock.getCatByNameFromUserRequest(text, chatId); //todo запрос по имени
                animal = cat;
                sendMessage = new SendMessage(chatId, cat.getNickName());
                sendMessage.replyMarkup(getAnimalInfoMarkup());
                sender.sendResponse(sendMessage);
            }
            default -> {
                choosePetMenu = 0;
                animal = null;
            }
        }
    }


}