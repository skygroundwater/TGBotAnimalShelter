package com.telegrambotanimalshelter.services;

import com.pengrad.telegrambot.model.Message;
import com.telegrambotanimalshelter.models.images.AppDocument;

public interface FileService {
    AppDocument processDoc(Message message);
}
