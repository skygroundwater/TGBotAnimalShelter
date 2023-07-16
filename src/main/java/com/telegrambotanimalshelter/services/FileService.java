package com.telegrambotanimalshelter.services;

import com.pengrad.telegrambot.model.Message;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;

import java.util.List;

public interface FileService<I extends AppImage> {
    I processDoc(I image, Message message);

    List<DogImage> getAllDogImages();

    List<CatImage> getAllCatImages();
}
