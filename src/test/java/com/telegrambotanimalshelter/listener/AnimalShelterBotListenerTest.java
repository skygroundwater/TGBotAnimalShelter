package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.listener.parts.checker.CallbackChecker;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.listener.parts.requests.ContactRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.listener.parts.volunteerblock.VolunteerBlock;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.repositories.PetOwnersRepository;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersServiceImpl;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerServiceImpl;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.telegrambotanimalshelter.utils.Constants.START_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalShelterBotListenerTest<A extends Animal, R extends Report, I extends AppImage> {



    @Test
    public void handleStartTest() throws IOException {

        String json = Files.readString(
                Path.of("C:\\Projects\\TGBotAnimalShelter\\src\\test\\resources\\com.telegrambotanimalshelter.listener\\update.json"));

        Update update = BotUtils.fromJson(json.replace("%text%", "/start"), Update.class);

        SendResponse sendResponse = BotUtils.fromJson("""
                {
                "ok": true
                }
                """, SendResponse.class);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        PetOwner petOwner = new PetOwner(1048847441L, "Олег",
                null, "skywater",
                LocalDateTime.of(2023, 7, 3, 13, 52, 47, 857000),
                false);

        PetOwner savedPetOwner = new PetOwner();

        savedPetOwner.setFirstName(petOwner.getFirstName());
        savedPetOwner.setLastName(petOwner.getLastName());
        savedPetOwner.setUserName(petOwner.getUserName());
        savedPetOwner.setRegisteredAt(petOwner.getRegisteredAt());
        savedPetOwner.setReportRequest(false);
        savedPetOwner.setContactRequest(false);
        savedPetOwner.setVolunteerChat(false);
        savedPetOwner.setId(petOwner.getId());




        verify(telegramBot, times(1)).execute(messageCaptor.capture());

        SendMessage actual = messageCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());

        assertThat(actual.getParameters().get("text")).isEqualTo(START_MESSAGE);

    }


}
