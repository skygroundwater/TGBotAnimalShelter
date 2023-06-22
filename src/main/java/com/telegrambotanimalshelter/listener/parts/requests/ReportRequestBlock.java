package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.telegrambotanimalshelter.listener.parts.keeper.Keeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.AppDocument;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.services.FileService;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petservice.PetService;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class ReportRequestBlock<A extends Animal> {

    private final MessageSender<A> sender;

    private final PetOwnersService petOwnersService;

    private final FileService fileService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private final PetService<Cat> catService;

    private final PetService<Dog> dogService;

    private final Keeper keeper;

    public ReportRequestBlock(MessageSender<A> sender, PetOwnersService petOwnersService,
                              FileService fileService, @Qualifier("catReportServiceImpl") ReportService<CatReport, Cat, CatImage> catReportService,
                              @Qualifier("dogReportServiceImpl") ReportService<DogReport, Dog, DogImage> dogReportService,
                              @Qualifier("catsServiceImpl") PetService<Cat> catService,
                              @Qualifier("dogsServiceImpl") PetService<Dog> dogService, Keeper keeper) {
        this.sender = sender;
        this.petOwnersService = petOwnersService;
        this.fileService = fileService;
        this.catReportService = catReportService;
        this.dogReportService = dogReportService;
        this.catService = catService;
        this.dogService = dogService;
        this.keeper = keeper;
    }

    public void choosePet(Long chatId, Shelter shelter){

        for(Map.Entry<Long, List<Cat>> entry: keeper.getCashedCats().entrySet()){
            entry.getValue().forEach(cat -> sender.choosePetMessage(chatId, (A) cat));
        }
        for(Map.Entry<Long, List<Dog>> entry: keeper.getCashedDogs().entrySet()){
            entry.getValue().forEach(dog -> sender.choosePetMessage(chatId, (A) dog));
        }

    }


    public void startReportFromPetOwner(Long chatId, Shelter shelter) {
        PetOwner petOwner = keeper.getCashedPetOwners().put(chatId, petOwnersService.setPetOwnerReportRequest(chatId, true));



        sender.sendMessage(chatId, "Итак, вы решили отправить-таки отчет по своему питомцу.\n" +
                "Следующим сообщением приложите его свежие фотографии." +
                "Чтобы прекратить процесс отправки отчета, воспользуйтесь командой /break");


    }

    public void reportFromPetOwnerBlock(Message message) {
        if (message.photo() == null) {
            Long chatId = message.chat().id();
            String text = message.text();
            String preFix = text.split(" ")[0];
            switch (preFix) {
                case "Диета:" -> sendMessageToTakeCommonStatus(chatId, message);
                case "Состояние:" -> {
                    sender.sendMessage(chatId, "Спасибо Вам за ваш отчет. Если будет что-то не так - волонтёр отпишетися вам. Желаем удачи.");
                    keeper.getCashedPetOwners().put(chatId, petOwnersService.setPetOwnerReportRequest(chatId, false));
                }
                case "/break" -> {
                    sender.sendStartMessage(chatId);
                    keeper.getCashedPetOwners().put(chatId, petOwnersService.setPetOwnerReportRequest(chatId, false));
                }
                default -> sendWarningLetter(chatId);
            }
        } else {
            sendMessageToTakeDiet(message.chat().id(), message);
        }
    }

    public boolean checkReportRequestStatus(Long petOwnerId) {
        try {
            return keeper.getCashedPetOwners().get(petOwnerId).isReportRequest();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void sendMessageToTakeDiet(Long chatId, Message message) {


        fileService.processDoc(message);


        sender.sendMessage(chatId, "Отлично. Теперь отправьте сообщешием повседневный рацион вашего животного. Префикс *Диета: *");
    }

    private void sendMessageToTakeCommonStatus(Long chatId, Message message) {
        sender.sendMessage(chatId, "Мы уже близки к завершению. Поделитесь общим состоянием животного.\n" +
                " Как его самочувствие и процесс привыкания к новому месту? Префикс *Состояние: *");
    }

    private void sendMessageToTakeChanges(Long chatId, Message message) {
        sender.sendMessage(chatId, "Последняя наша просьба - поделиться процессом изменения животного.\n" +
                "Как идет процесс восчпитания? Может быть, животное стало проявлять новые черты в своем поведении? Префикс *Изменения: *");

    }

    private void sendWarningLetter(Long chatId) {
        sender.sendMessage(chatId, "«Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно,\n" +
                " как необходимо. Пожалуйста, подойди ответственнее к этому занятию. \n" +
                "В противном случае, волонтеры приюта будут обязаны \n" +
                "самолично проверять условия содержания животного».");
    }
}