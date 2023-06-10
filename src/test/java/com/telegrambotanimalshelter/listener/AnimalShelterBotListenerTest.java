package com.telegrambotanimalshelter.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public
class AnimalShelterBotListenerTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private PetOwnersService petOwnersService;

    @InjectMocks
    private AnimalShelterBotListener animalShelterBotListener;


    @Test
    void init() {
    }

    @Test
    void handleStartTest() throws URISyntaxException, IOException {

        String json = Files.readString(Path.of(AnimalShelterBotListenerTest.class.getResource("update.json").toURI()));
        Update update = BotUtils.fromJson(json.replace("%text%", "/start"), Update.class);

        SendResponse sendResponse = BotUtils.fromJson("""
                {
                   "ok": true
                }
                """, SendResponse.class);
        when(telegramBot.execute(any())).thenReturn(sendResponse);
        animalShelterBotListener.process(Collections.singletonList(update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        assertThat(actual.getParameters().get("text")).isEqualTo("Здравствуйте! Вас приветсвует сеть приютов для животных города Астаны. \n" +
                "На данном этапе вы будете взимодействовать с нашим ботом. Выберите к какому приюту вы бы хотели обратиться");
    }
}