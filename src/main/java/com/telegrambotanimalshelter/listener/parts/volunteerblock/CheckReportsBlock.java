package com.telegrambotanimalshelter.listener.parts.volunteerblock;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@Component
public class CheckReportsBlock<A extends Animal, R extends Report> {

    private final CacheKeeper<A, R> keeper;

    private final MessageSender<A> sender;

    private final ConcurrentMap<Long, R> cachedCheckingReports;

    private final AuthorizationBlock<A, R> authorizationBlock;

    public CheckReportsBlock(CacheKeeper<A, R> keeper,
                             MessageSender<A> sender,
                             AuthorizationBlock<A, R> authorizationBlock) {
        this.keeper = keeper;
        this.sender = sender;
        this.authorizationBlock = authorizationBlock;
        this.cachedCheckingReports = new ConcurrentHashMap<>();
    }

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    public void startCheckingReports(Long chatId) {
        Volunteer volunteer = appointVolunteerToCheckReports(chatId);
        if (volunteer != null) {
            checkNoneCheckedReportsFromCacheKeeper(chatId);
        }
    }

    public Volunteer appointVolunteerToCheckReports(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        if (volunteer != null) {
            if (volunteer.isFree() && !volunteer.isCheckingReports() && volunteer.isInOffice()) {
                volunteer.setCheckingReports(true);
                volunteer.setFree(false);
                return cache().getVolunteers()
                        .put(chatId, keeper.getVolunteersService().putVolunteer(volunteer));
            }
            if (!volunteer.isFree() && volunteer.isInOffice() && !volunteer.isCheckingReports()) {
                volunteer.setCheckingReports(true);
                return cache().getVolunteers().put(chatId, keeper.getVolunteersService().putVolunteer(volunteer));
            }
        }
        return volunteer;
    }

    public void acceptReport(Long chatId) {
        R report = cachedCheckingReports.get(chatId);
        report.setCheckedByVolunteer(true);
        volunteerAcceptReport(chatId, report);
        cachedCheckingReports.remove(chatId);
        sender.sendResponse(new SendMessage(chatId, "Вы приняли отчёт. Продолжим или хотите прервать процесс?")
                .replyMarkup(volunteerKeyboardInOffice()));
    }

    public void rejectReport(Long chatId) {
        R report = cachedCheckingReports.get(chatId);
        volunteerRejectReport(chatId, report);
        cachedCheckingReports.remove(chatId);
        sender.sendMessage(report.getCopiedPetOwnerId(), """
                Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно,
                 как необходимо. Пожалуйста, подойди ответственнее к этому занятию.
                 В противном случае волонтеры приюта будут обязаны самолично проверять
                 условия содержания животного""");
        sender.sendResponse(new SendMessage(chatId, "Вы не приняли отчёт. Продолжим или хотите прервать процесс?")
                .replyMarkup(volunteerKeyboardInOffice()));
    }

    public Volunteer volunteerAcceptReport(Long volunteerId, R report) {
        cache().getCashedReports().remove(report);
        report.setCheckedByVolunteer(true);
        cache().getCashedReports().add(report);
        if (report instanceof DogReport dogReport) {
            keeper.getDogReportService().putReport(dogReport);
        } else if (report instanceof CatReport catReport) {
            keeper.getCatReportService().putReport(catReport);
        }
        Volunteer volunteer = cache().getVolunteers().get(volunteerId);
        volunteer.setCheckingReports(false);
        return cache().getVolunteers().put(volunteerId, volunteer);
    }

    public Volunteer volunteerRejectReport(Long volunteerId, R report) {
        cache().getCashedReports().remove(report);
        if (report instanceof DogReport dogReport) {
            cache().getDogsByPetOwnerId().get(dogReport.getCopiedPetOwnerId())
                    .stream()
                    .filter(d -> d.getId()
                            .equals(dogReport.getCopiedAnimalId()))
                    .findFirst()
                    .ifPresent(dog -> {
                        dog.setReported(false);
                        keeper.getDogService().putPet(dog);
                    });
            keeper.getDogReportService().deleteReportById(dogReport.getId());
        } else if (report instanceof CatReport catReport) {
            cache().getCatsByPetOwnerId().get(catReport.getCopiedPetOwnerId())
                    .stream()
                    .filter(c -> c.getId()
                            .equals(catReport.getCopiedAnimalId()))
                    .findFirst()
                    .ifPresent(cat -> {
                        cat.setReported(false);
                        keeper.getCatService().putPet(cat);
                    });
            keeper.getCatReportService().deleteReportById(catReport.getId());
        }
        cache().getVolunteers().entrySet().stream().peek(entry -> {
            if (entry.getKey().equals(volunteerId)) {
                entry.setValue(Stream.of(entry.getValue()).peek(vol ->
                        vol.setCheckingReports(false)).findFirst().get());
            }
        });
        return cache().getVolunteers().get(volunteerId);
    }

    public void forcedStopCheckReport(Long chatId) {
        authorizationBlock.volunteerWantsToGetOutFromOffice(chatId);
        cachedCheckingReports.remove(chatId);
        sender.sendMessage(chatId, "Вы вышли из кабинета волонтера");
        sender.sendStartMessage(chatId);
    }

    public void getOut(Long chatId) {
        authorizationBlock.volunteerWantsToGetOutFromOffice(chatId);
        cachedCheckingReports.remove(chatId);
        sender.sendMessage(chatId, "Вы вышли из кабинета волонтера");
        sender.sendStartMessage(chatId);
    }

    private void checkNoneCheckedReportsFromCacheKeeper(Long chatId) {
        List<R> reports = cache().getCashedReports()
                .stream().filter(r -> !r.isCheckedByVolunteer()).toList();
        if (reports.isEmpty()) {
            sender.sendResponse(new SendMessage(chatId,
                    "На данный момент отчетов усыновители не предоставляли")
                    .replyMarkup(volunteerKeyboardInOffice()));
            Volunteer volunteer = cache().getVolunteers().get(chatId);
            volunteer.setCheckingReports(false);
            cache().getVolunteers().put(chatId, volunteer);
        } else {
            for (R report : reports) {
                if (!report.getCopiedPetOwnerId().equals(chatId)) {
                    if (!report.isCheckedByVolunteer()) {
                        sendMessageWithUncheckedReportForVolunteer(chatId, report);
                        return;
                    }
                }
            }
            sender.sendMessage(chatId, "На данный момент отчетов усыновители не предоставляли");
        }
    }

    private void sendMessageWithUncheckedReportForVolunteer(Long chatId, R report) {
        sendReportPhoto(chatId, report);
        sendReportInfoByText(chatId, report);
        cachedCheckingReports.put(chatId, report);
    }

    private void sendReportPhoto(Long chatId, R report) {
        SendPhoto sendPhoto;
        if (report instanceof CatReport catReport) {
            Optional<CatImage> catImage = cache().getCatImages()
                    .stream()
                    .filter(image -> image.getCopiedReportId()
                            .equals(catReport.getId()))
                    .findFirst();
            if (catImage.isPresent()) {
                sendPhoto = new SendPhoto(chatId, catImage.get().getFileAsArrayOfBytes());
                sender.sendResponse(sendPhoto);
            }
        } else if (report instanceof DogReport dogReport) {
            Optional<DogImage> dogImage = cache().getDogImages().stream()
                    .filter(image -> image.getCopiedReportId()
                            .equals(dogReport.getId())).findFirst();
            if (dogImage.isPresent()) {
                sendPhoto = new SendPhoto(chatId, dogImage.get().getFileAsArrayOfBytes());
                sender.sendResponse(sendPhoto);
            }
        }
    }

    private void sendReportInfoByText(Long chatId, R report) {
        StringBuilder stringBuilder = new StringBuilder();
        if (report instanceof DogReport) {
            stringBuilder
                    .append("Отчет о собачке от ")
                    .append(cache().getPetOwnersById()
                            .get(report.getCopiedPetOwnerId())
                            .getFirstName())
                    .append("\n\n");
        } else if (report instanceof CatReport) {
            stringBuilder
                    .append("Отчет о кошке от ")
                    .append(cache().getPetOwnersById()
                            .get(report.getCopiedPetOwnerId())
                            .getFirstName())
                    .append("\n\n");
        }
        stringBuilder.append("*Диета питомца:* ")
                .append(report.getDiet())
                .append("\n\n").append("*Состояние питомца:* ")
                .append(report.getCommonDescriptionOfStatus())
                .append("\n\n").append("*Изменения: *")
                .append(report.getBehavioralChanges());
        SendMessage sendMessage =
                new SendMessage(chatId, stringBuilder.toString());
        sendMessage.replyMarkup(volunteerKeyboardWhileCheckingReport());
        sender.sendResponse(sendMessage);
    }

    private ReplyKeyboardMarkup volunteerKeyboardWhileCheckingReport() {
        return new ReplyKeyboardMarkup("Принять отчет")
                .addRow("Отклонить отчет")
                .addRow("Прервать проверку отчета")
                .oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup volunteerKeyboardInOffice() {
        return new ReplyKeyboardMarkup("Проверить отчеты")
                .addRow("Выйти из кабинета волонтёра")
                .oneTimeKeyboard(true);
    }
}