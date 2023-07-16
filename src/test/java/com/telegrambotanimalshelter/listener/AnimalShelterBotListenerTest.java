package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.telegrambotanimalshelter.listener.parts.checker.CallbackChecker;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.listener.parts.requests.ContactBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportBlock;
import com.telegrambotanimalshelter.listener.parts.requests.Chat;
import com.telegrambotanimalshelter.listener.parts.volunteerblock.VolunteerBlock;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersServiceImpl;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteersServiceImpl;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AnimalShelterBotListenerTest<A extends Animal, R extends Report, I extends AppImage> {

    @Mock
    TelegramBot telegramBot;

    @Mock
    Chat<A,R> chat;

    @Mock
    ContactBlock<A,R> contactBlock;

    @Mock
    VolunteerBlock<A,R,I> volunteerBlock;

    @Mock
    CallbackChecker<A,R,I> checker;

    @Mock
    MessageSender<A> sender;

    @Mock
    Logger logger;

    @Mock
    ReportBlock<A,R,I> reportBlock;

    @Mock
    AnimalShelterBotListener<A,R,I> out;

    AutoCloseable closeable;

    @Mock
    PetOwnersServiceImpl petOwnersService;

    @Mock
    VolunteersServiceImpl volunteerService;
    @Mock
    CacheKeeper<A,R> keeper;



}
