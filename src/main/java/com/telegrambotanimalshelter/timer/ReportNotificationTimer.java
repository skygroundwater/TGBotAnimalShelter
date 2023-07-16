package com.telegrambotanimalshelter.timer;

import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ReportNotificationTimer<A extends Animal> {

    private final MessageSender<A> sender;

    private final CacheKeeper<A, Report> keeper;

    @Autowired
    public ReportNotificationTimer(MessageSender<A> sender,
                                   CacheKeeper<A, Report> keeper) {
        this.sender = sender;
        this.keeper = keeper;
    }

    private Cache<A, ? extends Report> cache(){
        return keeper.getCache();
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void notificationToSendReport() {
        for (PetOwner petOwner : cache().getPetOwnersById().values()) {
            if (petOwner.isHasPets()) {
                StringBuilder stringBuilder = new StringBuilder();
                Long petOwnerId = petOwner.getId();
                for (Dog dog : cache().getDogsByPetOwnerId().get(petOwnerId)) {
                    if (!dog.isReported()) {
                        stringBuilder.append(dog.getNickName()).append(" ");
                    }
                }
                for (Cat cat : cache().getCatsByPetOwnerId().get(petOwnerId)) {
                    if (!cat.isReported()) {
                        stringBuilder.append(cat.getNickName()).append(" ");
                    }
                }
                sender.sendMessageToSendReport(petOwnerId, stringBuilder.toString());
            }
        }
    }

    @Scheduled(cron = "0 50 8 * * *")
    public String setAllAnimalsReportedToFalse() {
        for (Map.Entry<Long, List<Cat>> entry :
                keeper.getCache().getCatsByPetOwnerId().entrySet()) {
            for (Cat cat : entry.getValue()) {
                cat.setReported(false);
                keeper.getCatService().putPet(cat);
            }
        }
        for (Map.Entry<Long, List<Dog>> entry :
                keeper.getCache().getDogsByPetOwnerId().entrySet()) {
            for (Dog dog : entry.getValue()) {
                dog.setReported(false);
                keeper.getDogService().putPet(dog);
            }
        }
        return "У всех животных в базе данных и кеше обновлен статус об отчете";
    }
}
