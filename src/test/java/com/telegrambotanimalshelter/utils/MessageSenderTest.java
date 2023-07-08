package com.telegrambotanimalshelter.utils;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.models.animals.Animal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
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

import static com.telegrambotanimalshelter.utils.Constants.START_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageSenderTest<A extends Animal> {

    final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(
            new InlineKeyboardButton("Приют для собак ").callbackData("dog_shelter"),
            new InlineKeyboardButton("Приют для кошек ").callbackData("cat_shelter"))
            .addRow(new InlineKeyboardButton("Вы волонтёр").callbackData("i_am_volunteer"));

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private MessageSender<A> sender;

    public MessageSenderTest() throws IOException, URISyntaxException {
    }

    final String json = Files.readString(Path.of(MessageSenderTest.class.getResource("update.json").toURI()));


    @Test
    public void shouldSendStartMessage() {

        Update update = BotUtils.fromJson(json.replace(
                "%text%", "/start"), Update.class);
        when(telegramBot.execute(any())).thenReturn(BotUtils.fromJson(
                """
                        {
                        "ok": true
                        }
                        """, SendResponse.class
        ));
        sender.sendStartMessage(update.message().chat().id());

        ArgumentCaptor<SendMessage> captor =
                ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(1)).execute(captor.capture());
        SendMessage actual = captor.getValue();
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertEquals(actual.getParameters().get("text"), START_MESSAGE);
        Assertions.assertEquals(actual.getParameters().get("reply_markup"), inlineKeyboardMarkup);
    }

    @Test
    public void shouldSendMessage() {
        Update update = BotUtils.fromJson(json.replace(
                "%text%", "/start"), Update.class);
        when(telegramBot.execute(any())).thenReturn(BotUtils.fromJson(
                """
                        {
                        "ok": true
                        }
                        """, SendResponse.class
        ));
        String messageText = "Для проверки";
        sender.sendMessage(update.message().chat().id(), messageText);
        ArgumentCaptor<SendMessage> captor =
                ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(1)).execute(captor.capture());
        SendMessage actual = captor.getValue();
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertEquals(actual.getParameters().get("text"), messageText);
    }

    @Test
    public void shouldSendMessageForChat() {
        Update update = BotUtils.fromJson(json.replace(
                "%text%", "/start"), Update.class);
        when(telegramBot.execute(any())).thenReturn(BotUtils.fromJson(
                """
                        {
                        "ok": true
                        }
                        """, SendResponse.class
        ));
        String messageText = "Для чата волонтера и пользователя";
        sender.sendChatMessage(update.message().chat().id(), messageText);
        ArgumentCaptor<SendMessage> captor =
                ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(1))
                .execute(captor.capture());
        SendMessage actual = captor.getValue();
        assertThat(actual.getParameters().get("chat_id"))
                .isEqualTo(update.message().chat().id());
        Assertions.assertEquals(actual.getParameters().get("text"), messageText);
    }

    @SneakyThrows
    @Test
    public void shouldReturnSendResponse() {
        Update update = BotUtils.fromJson(json.replace(
                "%text%", "/start"), Update.class);
        when(telegramBot.execute(any())).thenReturn(BotUtils.fromJson(
                """
                        {
                        "ok": true
                        }
                        """, SendResponse.class
        ));
        String messageText = "Просто текст для теста";
        SendMessage sendMessage =
                new SendMessage(update.message().chat().id(), messageText);
        SendResponse sendResponse = sender.sendResponse(sendMessage);
        Mockito.verify(telegramBot, times(1))
                .execute(sendMessage);
        assertThat(sendResponse.isOk()).isEqualTo(true);
    }
}
