package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import static com.telegrambotanimalshelter.utils.Constants.sendMessage;


@Component
public class ReportPart {

    private final TelegramBot telegramBot;

    private final MessageSender sender;

    private final PetOwnersService petOwnersService;

    private final ContactRequestBlock contactBlock;

    private final Logger logger;

    public ReportPart(TelegramBot telegramBot, MessageSender sender, PetOwnersService petOwnersService, ContactRequestBlock contactBlock, Logger logger) {
        this.telegramBot = telegramBot;
        this.sender = sender;
        this.petOwnersService = petOwnersService;
        this.contactBlock = contactBlock;
        this.logger = logger;
    }

    public void startReportFromPetOwner(Long chatId, Shelter shelter) {
        petOwnersService.setPetOwnerReportRequest(chatId, true);
        sendMessage(sender, chatId, "Итак, вы решили отправить-таки отчет по своему питомцу.\n" +
                "Следующим сообщением приложите его фотографии, предварительно прописав префикс *Фото: *." +
                "Чтобы прекратить процесс отправки отчета, воспользуйтесь командой /break");
    }

    public void reportFromPetOwnerBlock(Long chatId, String prefix, Message message) {
        switch (prefix) {
            case "Фото:" -> sendMessageToTakeDiet(chatId, message);
            case "Диета:" -> sendMessageToTakeCommonStatus(chatId, message);
            case "Состояние:" -> {
                contactBlock.sendConfirmMessage(chatId, "Спасибо Вам за ваш отчет. Если будет что-то не так - волонтёр отпишетися вам. Желаем удачи.");
                petOwnersService.setPetOwnerReportRequest(chatId, false);
            }
            case "/break" -> {
                sender.sendStartMessage(chatId);
                petOwnersService.setPetOwnerReportRequest(chatId, false);
            }
            default -> sendWarningLetter(chatId);
        }
    }

    public void sendMessageToTakeDiet(Long chatId, Message message) {
        sendMessage(sender, chatId, "Отлично. Теперь отправьте сообщешием повседневный рацион вашего животного. Префикс *Диета: *");
    }

    public void sendMessageToTakeCommonStatus(Long chatId, Message message) {
        sendMessage(sender, chatId, "Мы уже близки к завершению. Поделитесь общим состоянием животного.\n" +
                " Как его самочувствие и процесс привыкания к новому месту? Префикс *Состояние: *");
    }

    public void sendMessageToTakeChanges(Long chatId, Message message) {
        sendMessage(sender, chatId, "Последняя наша просьба - поделиться процессом изменения животного.\n" +
                "Как идет процесс восчпитания? Может быть, животное стало проявлять новые черты в своем поведении? Префикс *Изменения: *");

    }

    public void sendWarningLetter(Long chatId) {
        sendMessage(sender, chatId, "«Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно,\n" +
                " как необходимо. Пожалуйста, подойди ответственнее к этому занятию. \n" +
                "В противном случае, волонтеры приюта будут обязаны \n" +
                "самолично проверять условия содержания животного».");
    }


}
