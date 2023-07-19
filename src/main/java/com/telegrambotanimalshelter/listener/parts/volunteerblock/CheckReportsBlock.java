package com.telegrambotanimalshelter.listener.parts.volunteerblock;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import java.util.List;
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
        return cache().getVolunteers().computeIfPresent(volunteerId, (key, volunteer) -> {
            cache().getCachedReports().forEach(rpt -> {
                if (report.equals(rpt)) {
                    rpt.setCheckedByVolunteer(true);
                    if (rpt instanceof DogReport dogReport) {
                        keeper.getDogReportService().putReport(dogReport);
                    } else if (rpt instanceof CatReport catReport) {
                        keeper.getCatReportService().putReport(catReport);
                    }
                }
            });
            volunteer.setCheckingReports(false);
            return volunteer;
        });
    }

    public Volunteer volunteerRejectReport(Long volunteerId, R report) {
        return cache().getVolunteers().computeIfPresent(volunteerId, (aLong, volunteer) -> {
            cache().getCachedReports().remove(report);
            if (report instanceof DogReport dogReport) {
                cache().getDogsByPetOwnerId().get(dogReport.getCopiedPetOwnerId()).forEach(dog -> {
                            if (dog.getId().equals(dogReport.getCopiedAnimalId())) {
                                dog.setReported(false);
                                keeper.getDogService().putPet(dog);
                                keeper.getDogReportService().deleteReportById(dogReport.getId());
                            }
                        }
                );
            } else if (report instanceof CatReport catReport) {
                cache().getCatsByPetOwnerId().get(catReport.getCopiedPetOwnerId())
                        .forEach(cat -> {
                            if (cat.getId().equals(catReport.getCopiedAnimalId())) {
                                cat.setReported(false);
                                keeper.getCatService().putPet(cat);
                                keeper.getCatReportService().deleteReportById(catReport.getId());
                            }
                        });
            }
            volunteer.setCheckingReports(false);
            return volunteer;
        });
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
        cache().getCachedReports().forEach(rpt -> {
            if (!rpt.isCheckedByVolunteer()) {
                if (!rpt.getCopiedPetOwnerId().equals(chatId)) {
                    sendMessageWithUncheckedReportForVolunteer(chatId, rpt);
                }
            }
        });
        if (!cachedCheckingReports.containsKey(chatId)) {
            cache().getVolunteers().forEach((aLong, volunteer) -> {
                if (aLong.equals(chatId)) {
                    volunteer.setCheckingReports(false);
                    sender.sendMessage(chatId, "На данный момент отчетов усыновители не предоставляли");
                }
            });
        }
    }

    private void sendMessageWithUncheckedReportForVolunteer(Long chatId, R report) {
        sendReportPhoto(chatId, report);
        sendReportInfoByText(chatId, report);
        cachedCheckingReports.put(chatId, report);
    }

    private void sendReportPhoto(Long chatId, R report) {
        if (report instanceof CatReport catReport) {
            cache().getCatImages().forEach(catImage -> {
                if (catImage.getCopiedReportId().equals(catReport.getId())) {
                    sender.sendResponse(new SendPhoto(chatId, catImage.getFileAsArrayOfBytes()));
                }
            });
        } else if (report instanceof DogReport dogReport) {
            cache().getDogImages().forEach(dogImage -> {
                if (dogImage.getCopiedReportId().equals(dogReport.getId())) {
                    sender.sendResponse(new SendPhoto(chatId, dogImage.getFileAsArrayOfBytes()));
                }
            });
        }
    }

    private void sendReportInfoByText(Long chatId, R report) {
        cache().getPetOwnersById().forEach((aLong, petOwner) -> {
            if (aLong.equals(report.getCopiedPetOwnerId())) {
                String catOrDog = null;
                if (report instanceof DogReport) {
                    catOrDog = "собачке";
                } else if (report instanceof CatReport) {
                    catOrDog = "кошке";
                }
                String info = String.format("""
                                Отчёт о %s от %s
                                                                
                                *Диета питомца:* %s

                                 *Состояние питомца:* %s

                                 *Изменения:* %s""",
                        catOrDog, petOwner.getFirstName(),
                        report.getDiet(), report.getCommonDescriptionOfStatus(),
                        report.getBehavioralChanges());
                sender.sendResponse(new SendMessage(chatId, info)
                        .replyMarkup(volunteerKeyboardWhileCheckingReport()));
            }
        });
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