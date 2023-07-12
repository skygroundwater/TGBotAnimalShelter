package com.telegrambotanimalshelter.listener.parts.volunteerblock;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class VolunteerBlock<A extends Animal, R extends Report, I extends AppImage> {

    private final VolunteerAndPetOwnerChat<A, R> chat;

    private final MessageSender<A> sender;

    private final CacheKeeper<A, R> cacheKeeper;

    private final HashMap<Long, R> cachedCheckingReports;


    public VolunteerBlock(VolunteerAndPetOwnerChat<A, R> chat,
                          MessageSender<A> sender,
                          CacheKeeper<A, R> cacheKeeper) {
        this.chat = chat;
        this.sender = sender;
        this.cacheKeeper = cacheKeeper;
        this.cachedCheckingReports = new HashMap<>();
    }

    private Cache<A, R> cache() {
        return cacheKeeper.getCache();
    }

    public void reportCheckingByVolunteerBlock(Long chatId, Message message) {
        String text = message.text();
        switch (text) {
            case "Проверить отчеты" -> startCheckingReports(chatId);
            case "Выйти из кабинета волонтёра" -> getOut(chatId);
            case "Принять отчет" -> acceptReport(chatId);
            case "Отклонить отчет" -> rejectReport(chatId);
            case "Прервать проверку отчета" -> forcedStopCheckReport(chatId);
            default -> sender.sendMessage(chatId,
                    "Если вы хотите выйти из своего кабинета, то нажмите кнопку <Прервать проверку отчета>");
        }
    }

    private boolean checkVolunteer(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        if (volunteer != null) {
            if (volunteer.isFree()) {
                return true;
            }
            return volunteer.isInOffice();
        }
        return false;
    }

    public Volunteer startWorkWithVolunteer(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        if (checkVolunteer(chatId)) {
            if (volunteer != null) {
                volunteer.setInOffice(true);
                volunteer.setFree(false);
                cache().getVolunteers().put(chatId, volunteer);
                cacheKeeper.getVolunteerService().putVolunteer(volunteer);
                sender.sendResponse(new SendMessage(chatId,
                        "Здравствуйте, " + volunteer.getFirstName() + ". Спасибо, что помогаете нам, мы очень это ценим")
                        .replyMarkup(volunteerKeyboardInOffice()));
                return volunteer;
            }
        } else {
            sender.sendMessage(chatId, "Нет вы не волонтёр");
        }
        return volunteer;
    }

    public boolean checkOfficeStatusForVolunteer(Long chatId) {
        Volunteer volunteer = cache().getVolunteers().get(chatId);
        if (volunteer != null) {
            return volunteer.isInOffice();
        } else return false;
    }

    private void acceptReport(Long chatId) {
        R report = cachedCheckingReports.get(chatId);
        report.setCheckedByVolunteer(true);
        cacheKeeper.volunteerAcceptReport(chatId, report);
        cachedCheckingReports.remove(chatId);
        sender.sendResponse(new SendMessage(chatId, "Вы приняли отчёт. Продолжим или хотите прервать процесс?")
                .replyMarkup(volunteerKeyboardInOffice()));
    }

    private void rejectReport(Long chatId) {
        R report = cachedCheckingReports.get(chatId);
        cacheKeeper.volunteerRejectReport(chatId, report);
        cachedCheckingReports.remove(chatId);
        sender.sendMessage(report.getCopiedPetOwnerId(), """
                Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно,
                 как необходимо. Пожалуйста, подойди ответственнее к этому занятию.
                 В противном случае волонтеры приюта будут обязаны самолично проверять
                 условия содержания животного""");
        sender.sendResponse(new SendMessage(chatId, "Вы не приняли отчёт. Продолжим или хотите прервать процесс?")
                .replyMarkup(volunteerKeyboardInOffice()));
    }


    private void forcedStopCheckReport(Long chatId) {
        cacheKeeper.volunteerWantsToGetOutFromOffice(chatId);
        cachedCheckingReports.remove(chatId);
        sender.sendMessage(chatId, "Вы вышли из кабинета волонтера");
        sender.sendStartMessage(chatId);
    }

    private void getOut(Long chatId) {
        cacheKeeper.volunteerWantsToGetOutFromOffice(chatId);
        cachedCheckingReports.remove(chatId);
        sender.sendMessage(chatId, "Вы вышли из кабинета волонтера");
        sender.sendStartMessage(chatId);
    }

    public void startCheckingReports(Long chatId) {
        Volunteer volunteer = cacheKeeper.appointVolunteerToCheckReports(chatId);
        if (volunteer != null) {
            checkNoneCheckedReportsFromCacheKeeper(chatId);
        }
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
                    .append("Отчет о собачке от ")
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
