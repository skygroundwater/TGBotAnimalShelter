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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
@MockitoSettings(strictness = Strictness.LENIENT)
public class AnimalShelterBotListenerTest<A extends Animal, R extends Report, I extends AppImage> {

    @Mock
    TelegramBot telegramBot;

    @Mock
    VolunteerAndPetOwnerChat<A,R> chat;

    @Mock
    ContactRequestBlock<A,R> contactBlock;

    @Mock
    VolunteerBlock<A,R,I> volunteerBlock;

    @Mock
    CallbackChecker<A,R,I> checker;

    @Mock
    MessageSender<A> sender;

    @Mock
    Logger logger;

    @Mock
    ReportRequestBlock<A,R,I> reportBlock;

    @Mock
    AnimalShelterBotListener<A,R,I> out;

    AutoCloseable closeable;

    @Mock
    PetOwnersServiceImpl petOwnersService;

    @Mock
    VolunteerServiceImpl volunteerService;
    @Mock
    CacheKeeper<A,R> keeper;



}
