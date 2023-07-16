package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.telegrambotanimalshelter.utils.ConstantsForTesting.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContactBlockTest<A extends Animal, R extends Report> {

    @Mock
    private MessageSender<A> sender;

    @Mock
    private CacheKeeper<A, R> keeper;

    @Mock
    private PetOwnersService petOwnersService;

    @InjectMocks
    private ContactBlock<A, R> out;

    private Cache<A, R> cache;

    final String json = Files.readString(Path.of(
            ContactBlockTest.class
                    .getResource("update.json").toURI()));

    final String json2 = Files.readString(Path.of(
            ContactBlockTest.class
                    .getResource("update2.json").toURI()));

    final String messageJson = Files.readString(Path.of(
            ContactBlockTest.class
                    .getResource("message.json").toURI()));

    ContactBlockTest() throws IOException, URISyntaxException {
    }

    @BeforeEach
    void setUp() {
        cache = new Cache<>();
        when(keeper.getCache()).thenReturn(cache);
    }




}