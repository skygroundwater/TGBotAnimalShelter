package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.telegrambotanimalshelter.utils.MessageSender;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import org.springframework.stereotype.Component;


@Component
public class ReportRequestBlock {

    private final MessageSender sender;

    private final PetOwnersService petOwnersService;

    private final ContactRequestBlock contactBlock;

    public ReportRequestBlock(MessageSender sender, PetOwnersService petOwnersService, ContactRequestBlock contactBlock) {
        this.sender = sender;
        this.petOwnersService = petOwnersService;
        this.contactBlock = contactBlock;
    }

    public void startReportFromPetOwner(Long chatId, Shelter shelter) {
        petOwnersService.setPetOwnerReportRequest(chatId, true);
        sender.sendMessage(chatId, "Итак, вы решили отправить-таки отчет по своему питомцу.\n" +
                "Следующим сообщением приложите его фотографии, предварительно прописав префикс *Фото: *." +
                "Чтобы прекратить процесс отправки отчета, воспользуйтесь командой /break");
    }

    public void reportFromPetOwnerBlock(Long chatId, String prefix, Message message) {
        switch (prefix) {
            case "Фото:" -> sendMessageToTakeDiet(chatId, message);
            case "Диета:" -> sendMessageToTakeCommonStatus(chatId, message);
            case "Состояние:" -> {
                sender.sendMessage(chatId, "Спасибо Вам за ваш отчет. Если будет что-то не так - волонтёр отпишетися вам. Желаем удачи.");
                petOwnersService.setPetOwnerReportRequest(chatId, false);
            }
            case "/break" -> {
                sender.sendStartMessage(chatId);
                petOwnersService.setPetOwnerReportRequest(chatId, false);
            }
            default -> sendWarningLetter(chatId);
        }
    }

    public boolean checkReportRequestStatus(Long petOwnerId){
        return petOwnersService.checkReportRequestStatus(petOwnerId);
    }

    public void sendMessageToTakeDiet(Long chatId, Message message) {
        sender.sendMessage(chatId, "Отлично. Теперь отправьте сообщешием повседневный рацион вашего животного. Префикс *Диета: *");
    }

    public void sendMessageToTakeCommonStatus(Long chatId, Message message) {
        sender.sendMessage(chatId, "Мы уже близки к завершению. Поделитесь общим состоянием животного.\n" +
                " Как его самочувствие и процесс привыкания к новому месту? Префикс *Состояние: *");
    }

    public void sendMessageToTakeChanges(Long chatId, Message message) {
        sender.sendMessage(chatId, "Последняя наша просьба - поделиться процессом изменения животного.\n" +
                "Как идет процесс восчпитания? Может быть, животное стало проявлять новые черты в своем поведении? Префикс *Изменения: *");

    }

    public void sendWarningLetter(Long chatId) {
        sender.sendMessage(chatId, "«Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно,\n" +
                " как необходимо. Пожалуйста, подойди ответственнее к этому занятию. \n" +
                "В противном случае, волонтеры приюта будут обязаны \n" +
                "самолично проверять условия содержания животного».");
    }
}