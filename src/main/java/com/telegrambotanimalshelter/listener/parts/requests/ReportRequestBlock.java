package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.FileService;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petservice.PetService;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class ReportRequestBlock<A extends Animal, R extends Report, I extends AppImage> {

    private final MessageSender<A> sender;

    private final PetOwnersService petOwnersService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private final FileService<I> fileService;

    private final CacheKeeper<A, R> keeper;

    private HashMap<Long, ArrayList<A>> cashedNoneReportedPetNames;


    public ReportRequestBlock(MessageSender<A> sender, PetOwnersService petOwnersService,
                              @Qualifier("catReportServiceImpl") ReportService<CatReport, Cat, CatImage> catReportService,
                              @Qualifier("dogReportServiceImpl") ReportService<DogReport, Dog, DogImage> dogReportService,
                              FileService<I> fileService,
                              CacheKeeper<A, R> keeper) {
        this.sender = sender;
        this.petOwnersService = petOwnersService;
        this.catReportService = catReportService;
        this.dogReportService = dogReportService;
        this.fileService = fileService;
        this.keeper = keeper;
        this.cashedNoneReportedPetNames = new HashMap<>();
    }

    private void chooseAnyPetMessages(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Выберите животное, на которого хотите отправить отет по его кличке");
        ReplyKeyboardMarkup choosePetMarkup = new ReplyKeyboardMarkup(new KeyboardButton("Прервать отчет"));
        cashedNoneReportedPetNames.put(chatId, new ArrayList<>());
        for (Cat cat : keeper.getCatsByPetOwnerIdFromCache(chatId)) {
            if (!cat.isReported()) {
                choosePetMarkup.addRow(cat.getNickName());
                cashedNoneReportedPetNames.get(chatId).add((A) cat);
            }
            for (Dog dog : keeper.getDogByPetOwnerIdFromCache(chatId)) {
                if (!dog.isReported()) {
                    choosePetMarkup.addRow(dog.getNickName());
                    cashedNoneReportedPetNames.get(chatId).add((A) dog);
                }
                sendMessage.replyMarkup(choosePetMarkup);
            }
        }
        sender.sendResponse(sendMessage);
    }

    public void startReportFromPetOwner(Long chatId) {
        if(keeper.getPetOwners().get(chatId).isHasPets()) {
            keeper.getPetOwners().put(chatId, petOwnersService.setPetOwnerReportRequest(chatId, true));
            chooseAnyPetMessages(chatId);
        }else sender.sendMessage(chatId, "У вас нет животных");
    }

    public boolean checkIsMessageANameOfPet(Long chatId, Message message) {
        for (A animal : cashedNoneReportedPetNames.get(chatId)) {
            if (animal.getNickName().equals(message.text())) {
                if (animal instanceof Dog) {
                    keeper.createReportForAnimal(chatId, animal);
                    return true;
                } else if (animal instanceof Cat) {
                    keeper.createReportForAnimal(chatId, animal);
                    return true;
                }
                sendMessageToTakePhoto(chatId);
            }
        }
        return false;
    }

    public boolean checkIsMessageAPhoto(Long chatId, Message message) {
        if (message.photo() != null) {
            R report = keeper.getActualReportsByPetOwnerId().get(chatId);
            if (report instanceof CatReport) {
                List<CatImage> images = ((CatReport) report).getImages();
                CatImage catImage = new CatImage();
                images.add((CatImage) fileService.processDoc((I) catImage, message));
                ((CatReport) report).setImages(images);
                return true;
            } else if (report instanceof DogReport) {
                List<DogImage> images = ((DogReport) report).getImages();
                DogImage dogImage = new DogImage();
                images.add((DogImage) fileService.processDoc((I) dogImage, message));
                ((DogReport) report).setImages(images);
                return true;
            }
            sendMessageToTakeDiet(chatId);
        }
        return false;
    }

    public void reportFromPetOwnerBlock(Long chatId, Message message) {

        if (checkIsMessageANameOfPet(chatId, message)) {
            sendMessageToTakePhoto(chatId);
            return;
        }

        if (checkIsMessageAPhoto(chatId, message)) {
            sendMessageToTakeDiet(chatId);
            return;
        }

        String text = message.text();
        String preFix = text.split(" ")[0];

        switch (preFix) {
            case "Диета:" -> {
                sendMessageToTakeCommonStatus(chatId, message);
            }
            case "Состояние:" -> {
                sendMessageToTakeChanges(chatId, message);
            }
            case "Изменения:" -> {
                stopReport(chatId, message);
            }
            case "/break" -> {
                forcedStopReport(chatId);
            }
            default -> sendWarningLetter(chatId);
        }
    }

    public boolean checkReportRequestStatus(Long petOwnerId) {
        try {
            return keeper.getPetOwners().get(petOwnerId).isReportRequest();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void forcedStopReport(Long chatId) {
        sender.sendMessage(chatId, "Вы прервали отправку отчета. Пожалуйста, не забудьте отправить его позже.");
        keeper.getPetOwners().put(chatId, petOwnersService.setPetOwnerReportRequest(chatId, false));
    }

    private void stopReport(Long chatId, Message message) {
        R report = keeper.getActualReportsByPetOwnerId().get(chatId);
        report.setBehavioralChanges(message.text());
        if (report instanceof CatReport) {
            CatImage catImage = ((CatReport) report).getImages().get(0);
            catImage.setCat((Cat) keeper.getActualPetsInReportProcess().get(chatId));
            catImage.setCatReport((CatReport) report);
            catReportService.putReport((CatReport) report);
            fileService.saveCatImage(catImage);
        } else if (report instanceof DogReport) {
            DogImage dogImage = ((DogReport) report).getImages().get(0);
            dogImage.setDog((Dog) keeper.getActualPetsInReportProcess().get(chatId));
            dogImage.setDogReport((DogReport) report);
            dogReportService.putReport((DogReport) report);
            fileService.saveDogImage(dogImage);
        }
        sender.sendMessage(chatId, "Спасибо Вам за ваш отчет. Если будет что-то не так - волонтёр отпишетися вам. Желаем удачи.");
        keeper.getPetOwners().put(chatId, petOwnersService.setPetOwnerReportRequest(chatId, false));
    }

    public void sendMessageToTakePhoto(Long chatId) {
        sender.sendMessage(chatId, "Отлично, вы начали отправлять запрос. Следующим сообщением приложите, пожалуйста *фото* питомца.\n" +
                " Пожалуйста, позаботьтесь о хорошем освещении при фотографировании");
    }

    private void sendMessageToTakeDiet(Long chatId) {
        sender.sendMessage(chatId, "Отлично. Теперь отправьте сообщешием повседневный рацион вашего животного. Префикс *Диета: *");
    }

    private void sendMessageToTakeCommonStatus(Long chatId, Message message) {
        sender.sendMessage(chatId, "Мы уже близки к завершению. Поделитесь общим состоянием животного.\n" +
                " Как его самочувствие и процесс привыкания к новому месту? Префикс *Состояние: *");
        keeper.getActualReportsByPetOwnerId().get(chatId).setDiet(message.text());
    }

    private void sendMessageToTakeChanges(Long chatId, Message message) {
        sender.sendMessage(chatId, "Последняя наша просьба - поделиться процессом изменения животного.\n" +
                "Как идет процесс восчпитания? Может быть, животное стало проявлять новые черты в своем поведении? Префикс *Изменения: *");
        keeper.getActualReportsByPetOwnerId().get(chatId).setCommonDescriptionOfStatus(message.text());
    }

    private void sendWarningLetter(Long chatId) {
        sender.sendMessage(chatId, "«Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно,\n" +
                " как необходимо. Пожалуйста, подойди ответственнее к этому занятию. \n" +
                "В противном случае, волонтеры приюта будут обязаны \n" +
                "самолично проверять условия содержания животного».");
    }
}