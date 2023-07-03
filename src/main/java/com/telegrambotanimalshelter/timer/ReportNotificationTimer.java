package com.telegrambotanimalshelter.timer;

import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReportNotificationTimer<A extends Animal> {

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    private final MessageSender<A> sender;

    private final CacheKeeper<A, Report> cacheKeeper;


    @Autowired
    public ReportNotificationTimer(@Qualifier("dogReportServiceImpl") ReportService<DogReport, Dog, DogImage> dogReportService,
                                   @Qualifier("catReportServiceImpl") ReportService<CatReport, Cat, CatImage> catReportService,
                                   MessageSender<A> sender, CacheKeeper<A, Report> cacheKeeper) {
        this.dogReportService = dogReportService;
        this.catReportService = catReportService;
        this.sender = sender;
        this.cacheKeeper = cacheKeeper;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void notificationToSendReport() {
        StringBuilder stringBuilder = new StringBuilder();

        for (PetOwner petOwner : cacheKeeper.getPetOwnersById().values()) {
            if (petOwner.isHasPets()) {
                Long petOwnerId = petOwner.getId();
                for (Dog dog : cacheKeeper.getDogs().get(petOwnerId)) {
                    if (!dog.isReported()) {
                        stringBuilder.append(dog.getNickName()).append(" ");
                    }
                }
                for (Cat cat : cacheKeeper.getCats().get(petOwnerId)) {
                    if (!cat.isReported()) {
                        stringBuilder.append(cat.getNickName()).append(" ");
                    }
                }
                sender.sendMessageToSendReport(petOwnerId, stringBuilder.toString());
            }
        }
    }

    @Scheduled(cron = "0 50 7 * * *")
    public void resetReporting() {
        cacheKeeper.setAllAnimalsReportedToFalse();
    }

    private void checkLastReportFromPet(Long chatId, A animal) {
        List<? extends Report> reports = new ArrayList<>();
        if (animal instanceof Dog) {
            reports = getReportsFromDog((Dog) animal);
        } else if (animal instanceof Cat) {
            reports = getReportsFromCat((Cat) animal);
        }
        if (reports.isEmpty() || reports.get(reports.size() - 1).getDate()
                .equals(LocalDateTime.now().toLocalDate().minusDays(1))) {
            sender.sendMessageToSendReport(chatId, "animal");
        }
    }

    private List<DogReport> getReportsFromDog(Dog dog) {
        return dogReportService.findReportsFromPet(dog);
    }

    private List<CatReport> getReportsFromCat(Cat cat) {
        return catReportService.findReportsFromPet(cat);
    }
}
