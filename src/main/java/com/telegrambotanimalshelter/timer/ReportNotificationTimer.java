package com.telegrambotanimalshelter.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petservice.PetService;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ReportNotificationTimer<A extends Animal> {

    private final PetService<Cat> catsService;

    private final PetService<Dog> dogsService;

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    private final PetOwnersService petOwnersService;

    private final TelegramBot telegramBot;


    @Autowired
    public ReportNotificationTimer(@Qualifier("catsServiceImpl") PetService<Cat> catsService,
                                   @Qualifier("dogsServiceImpl") PetService<Dog> dogsService,
                                   @Qualifier("dogReportServiceImpl") ReportService<DogReport, Dog, DogImage> dogReportService,
                                   @Qualifier("catReportServiceImpl") ReportService<CatReport, Cat, CatImage> catReportService,
                                   PetOwnersService petOwnersService, TelegramBot telegramBot) {
        this.catsService = catsService;
        this.dogsService = dogsService;
        this.dogReportService = dogReportService;
        this.catReportService = catReportService;
        this.petOwnersService = petOwnersService;
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void notificationToSendReport() {
        for (PetOwner petOwner : petOwnersService.findActualPetOwners()) {
            for (Cat cat : getCatsFromPetOwner(petOwner)) {
                checkLastReportFromPet(petOwner.getId(), (A) cat, catsService.getShelter());
            }
            for (Dog dog : getDogsFromPetOwner(petOwner)) {
                checkLastReportFromPet(petOwner.getId(), (A) dog, dogsService.getShelter());
            }
        }
    }

    private void checkLastReportFromPet(Long chatId, A animal, Shelter shelter) {
        List<? extends Report> reports = new ArrayList<>();
        if (animal instanceof Dog) {
            reports = getReportsFromDog((Dog) animal);
        } else if (animal instanceof Cat) {
            reports = getReportsFromCat((Cat) animal);
        }
        if (reports.isEmpty() || reports.get(reports.size() - 1).getDate().equals(LocalDateTime.now().toLocalDate().minusDays(1))) {
            sendMessageToSendReport(chatId, animal.getNickName(), shelter);
        }
    }

    private List<Cat> getCatsFromPetOwner(PetOwner petOwner) {
        return catsService.findPetsByPetOwner(petOwner);
    }

    private List<Dog> getDogsFromPetOwner(PetOwner petOwner) {
        return dogsService.findPetsByPetOwner(petOwner);
    }

    private List<DogReport> getReportsFromDog(Dog dog) {
        return dogReportService.findReportsFromPet(dog);
    }

    private List<CatReport> getReportsFromCat(Cat cat) {
        return catReportService.findReportsFromPet(cat);
    }

    private void sendMessageToSendReport(Long chatId, String petName, Shelter shelter) {
        String text = "Пришлите отчет по вашему подопечному: *" + petName
                + "*\n Ждём информации сегодня до конца дня";

        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.parseMode(ParseMode.Markdown);
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Прислать отчет")
                        .callbackData(shelter.getName() + "_report")
        ));
        telegramBot.execute(sendMessage);
    }
}
