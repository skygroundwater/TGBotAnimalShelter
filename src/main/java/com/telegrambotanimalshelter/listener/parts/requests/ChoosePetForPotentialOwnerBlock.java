package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petphotoservice.PetPhotoService;
import com.telegrambotanimalshelter.services.petservice.CatsServiceImpl;
import com.telegrambotanimalshelter.services.petservice.DogsServiceImpl;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Component
public class ChoosePetForPotentialOwnerBlock<A extends Animal, R extends Report> {

    boolean sendNotShelteredAnimalsFlag = false;
    private final MessageSender<A> sender;

    private final CatsServiceImpl catsService;
    private final DogsServiceImpl dogsService;
    private final CatsRepository catsRepository;

    private final DogsRepository dogsRepository;

    private final PetOwnersService petOwnersService;

    private final CacheKeeper<A, R> cacheKeeper;

    private final PetPhotoService petPhotoService;

    public ChoosePetForPotentialOwnerBlock(MessageSender<A> sender,
                                           CatsServiceImpl catsService, DogsServiceImpl dogsService, CatsRepository catsRepository,
                                           DogsRepository dogsRepository, PetOwnersService petOwnersService, CacheKeeper<A, R> cacheKeeper, PetPhotoService petPhotoService) {
        this.sender = sender;
        this.catsService = catsService;
        this.dogsService = dogsService;
        this.catsRepository = catsRepository;
        this.dogsRepository = dogsRepository;
        this.petOwnersService = petOwnersService;
        this.cacheKeeper = cacheKeeper;
        this.petPhotoService = petPhotoService;
    }

    private List<Cat> getAllNotShelteredCats() {
        return catsRepository.findCatsBySheltered(false);
    }

    private List<Dog> getAllNotShelteredDogs() {
        return dogsRepository.findDogsBySheltered(false);
    }

    public boolean sendNotShelteredAnimals(String data, Long chatId) {
        StringBuilder builder = new StringBuilder();

        if (data.equals("_get_cat")) {
            List<Cat> allNotShelteredCats = getAllNotShelteredCats();
            if (allNotShelteredCats.size() == 0) {
                builder.append("На данный момент все животные нашли своих хозяев :)\n");
            } else {
                sendNotShelteredAnimalsFlag = true;
                builder.append("В нашем приюте проживают:\n");
                for (Cat cats : allNotShelteredCats) {
                    builder.append(cats.getNickName()).append("\n");
                }
                builder.append("Информацию о каком животном вы бы хотели посмотреть?\n");
            }

        } else {
            List<Dog> allNotShelteredDogs = getAllNotShelteredDogs();
            if (allNotShelteredDogs.size() == 0) {
                builder.append("На данный момент все животные нашли своих хозяев :)\n");
            } else {
                sendNotShelteredAnimalsFlag = true;
                builder.append("В нашем приюте проживают:\n");
                for (Dog dogs : allNotShelteredDogs) {
                    builder.append(dogs.getNickName()).append("\n");
                }
                builder.append("Информацию о каком животном вы бы хотели посмотреть?\n");
            }
        }
        sender.sendMessage(chatId, builder.toString());

        return sendNotShelteredAnimalsFlag;
    }

    public Dog getDogByNameFromUserRequest(String name, Long chatId) {
        SendMessage sendMessage;

        Dog dog = dogsRepository.findDogsByNickName(name);
        if (dog == null) {
            sendMessage = new SendMessage(chatId, "Собаки с таким именем нет в базе или задан неверный запрос");
            sender.sendResponse(sendMessage);
            throw new NotFoundInDataBaseException("Собаки с таким именем нет в базе или задан неверный запрос");
        } else {
            return dog;
        }
    }

    public Cat getCatByNameFromUserRequest(String name, Long chatId) {
        SendMessage sendMessage;
        Cat cat = catsRepository.findCatsByNickName(name);
        if (cat == null) {
            sendMessage = new SendMessage(chatId, "Кошки с таким именем нет в базе или задан неверный запрос");
            sender.sendResponse(sendMessage);
            throw new NotFoundInDataBaseException("Кошки с таким именем нет в базе или задан неверный запрос");
        } else {
            return cat;
        }
    }

    public boolean checkNotShelteredAnimals() {
        return sendNotShelteredAnimalsFlag;
    }

    public void getAnimalInfo(Animal animal, Long chatId) {
        SendMessage sendMessage;
        if (animal != null) {
            String about = animal.getAbout();
            sendMessage = new SendMessage(chatId, Objects.requireNonNullElse(about, "Информация отсутствует"));

        } else {
            sendMessage = new SendMessage(chatId, "Повторите запрос или свяжитесь с волонтером");
        }
        sender.sendResponse(sendMessage);
    }

    public void getPetPhotoFromShelter(Animal animal, Long chatId) {
        File petPhoto = petPhotoService.getPetPhoto(animal, animal.getNickName());
        if (petPhoto != null) {
            SendPhoto sendPhoto = new SendPhoto(chatId, petPhoto);
            sender.sendResponse(sendPhoto);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Фото отсутствует");
            sender.sendResponse(sendMessage);
        }
    }

    public void getPetFromShelter(Animal animal, Long chatId) {
        PetOwner petOwner = petOwnersService.findPetOwner(chatId);
        SendMessage sendMessage;

        if (animal != null && petOwner != null) {
            petOwner.setHasPets(true);
            petOwnersService.putPetOwner(petOwner);

            if (animal instanceof Cat) {
                ((Cat) animal).setPetOwner(petOwner);
                animal.setSheltered(true);
                catsService.putPet((Cat) animal);
            } else {
                ((Dog) animal).setPetOwner(petOwner);
                animal.setSheltered(true);
                dogsService.putPet((Dog) animal);
            }
            sendMessage = new SendMessage(chatId, "Поздравляю, у вас появился новый друг " + animal.getNickName());
            sender.sendResponse(sendMessage);
        }
    }
}
