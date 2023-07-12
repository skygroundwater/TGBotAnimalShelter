package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
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
import java.util.*;

@Component
public class ChoosePetForPotentialOwnerBlock<A extends Animal, R extends Report> {
    private final MessageSender<A> sender;

    private final CatsServiceImpl catsService;
    private final DogsServiceImpl dogsService;
    private final CatsRepository catsRepository;

    private final DogsRepository dogsRepository;

    private final PetOwnersService petOwnersService;

    private final CacheKeeper<A, R> cacheKeeper;

    private final PetPhotoService petPhotoService;

    private Map<Long, List<A>> cashedNoneShelteredAnimalsForChoosing;

    private Map<Long, A> showingAnimalsByPetOwnerID;

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
        this.cashedNoneShelteredAnimalsForChoosing = new HashMap<>();
        this.showingAnimalsByPetOwnerID = new HashMap<>();
    }

    private Cache<A, R> cache() {
        return cacheKeeper.getCache();
    }

    public SendResponse startChoosingOneOfPets(Long chatId, ShelterType shelterType) {
        cache().getPetOwnersById().put(chatId,
                petOwnersService.setChoosingPets(chatId, true));
        ReplyKeyboardMarkup replyMarkup
                = new ReplyKeyboardMarkup("Не выбирать животное")
                .oneTimeKeyboard(true);
        SendMessage sendMessage = new SendMessage(
                chatId, "*Вы можете выбрать животное из списка*");
        sendMessage.parseMode(ParseMode.Markdown);
        cashedNoneShelteredAnimalsForChoosing.put(chatId, new ArrayList<>());
        switch (shelterType) {
            case DOGS_SHELTER -> cache().getCachedAnimals().stream().filter(
                    animal -> animal instanceof Dog && !animal.isSheltered()
            ).forEach(dog -> {
                replyMarkup.addRow(dog.getNickName());
                cashedNoneShelteredAnimalsForChoosing.get(chatId).add(dog);
            });
            case CATS_SHELTER -> cache().getCachedAnimals().stream().filter(
                    animal -> animal instanceof Cat && !animal.isSheltered()
            ).forEach(cat -> {
                replyMarkup.addRow(cat.getNickName());
                cashedNoneShelteredAnimalsForChoosing.get(chatId).add(cat);
            });
        }
        sendMessage.replyMarkup(replyMarkup);
        return sender.sendResponse(sendMessage);
    }

    private SendResponse petOwnerIsLookingInfoAboutPetBlock(Long chatId, Message message) {
        String text = message.text();
        A animal = showingAnimalsByPetOwnerID.get(chatId);
        switch (text) {
            case "Посмотреть информацию о будущем питомце":
                return getAnimalInfo(animal, chatId);
            case "Посмотреть фото будущего питомца":
                return getPetPhotoFromShelter(animal, chatId);
            case "Приютить животное":
                return getPetFromShelter(animal, chatId);
            case "Назад к выбору животного": {
                if (animal instanceof Cat) {
                    return startChoosingOneOfPets(chatId, ShelterType.CATS_SHELTER);
                } else if (animal instanceof Dog) {
                    return startChoosingOneOfPets(chatId, ShelterType.DOGS_SHELTER);
                }
            }
            default: {
                return sender.sendResponse(new SendMessage(
                        chatId, "Если пока не решили и хотите подумать, то нажмите кнопку *Назад к выбору животного*"
                ).parseMode(ParseMode.Markdown));
            }
        }
    }

    public SendResponse choosingPetForPetOwnerBlock(Long chatId, Message message) {
        if (checkIfPetOwnerIsLookingInfoAboutPet(chatId)) {
            return petOwnerIsLookingInfoAboutPetBlock(chatId, message);
        }
        String text = message.text();
        if (text.equals("Не выбирать животное")) {
            sender.sendStartMessage(chatId);
            return stopChoosing(chatId);
        }
        return checkPetNames(chatId, text);
    }

    private SendResponse checkPetNames(Long chatId, String nameInTextFromMessage) {
        SendMessage sendMessage;
        for (A animal : cashedNoneShelteredAnimalsForChoosing.get(chatId)) {
            if (nameInTextFromMessage.equals(animal.getNickName())) {
                showingAnimalsByPetOwnerID.put(chatId, animal);
                cache().getPetOwnersById().put(chatId,
                        petOwnersService.setLookingAboutPet(chatId, true));
                sendMessage = new SendMessage(chatId, "Вы выбрали *" + animal.getNickName() + "*")
                        .replyMarkup(new ReplyKeyboardMarkup(
                                new KeyboardButton("Посмотреть информацию о будущем питомце"))
                                .addRow(new KeyboardButton("Посмотреть фото будущего питомца"))
                                .addRow(new KeyboardButton("Приютить животное"))
                                .addRow(new KeyboardButton("Назад к выбору животного")));
                return sender.sendResponse(sendMessage);
            }
        }
        sendMessage = new SendMessage(chatId, "Вы ввели неправильное имя");
        return sender.sendResponse(sendMessage);
    }

    public SendResponse stopChoosing(Long chatId) {
        showingAnimalsByPetOwnerID.remove(chatId);
        cache().getPetOwnersById().put(chatId,
                petOwnersService.setChoosingPets(chatId, false));
        return sender.sendResponse(new SendMessage(chatId, "Вы даже не решились посмотреть..."));
    }

    public boolean checkIfPetOwnerChoosingPet(Long chatId) {
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
        if (petOwner != null) return petOwner.isChoosingPet();
        else return false;
    }

    public boolean checkIfPetOwnerIsLookingInfoAboutPet(Long chatId) {
        return cache().getPetOwnersById().get(chatId).isLookingAboutPet();
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

    public SendResponse getAnimalInfo(Animal animal, Long chatId) {
        SendMessage sendMessage;
        if (animal != null) {
            String about = animal.getAbout();
            sendMessage = new SendMessage(chatId, Objects.requireNonNullElse(about, "Информация отсутствует"));
        } else {
            sendMessage = new SendMessage(chatId, "Повторите запрос или свяжитесь с волонтером");
        }
        return sender.sendResponse(sendMessage);
    }

    public SendResponse getPetPhotoFromShelter(Animal animal, Long chatId) {
        File petPhoto = petPhotoService.getPetPhoto(animal, animal.getNickName());
        if (petPhoto != null) {
            SendPhoto sendPhoto = new SendPhoto(chatId, petPhoto);
            return sender.sendResponse(sendPhoto);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Фото отсутствует");
            return sender.sendResponse(sendMessage);
        }
    }

    public SendResponse getPetFromShelter(A animal, Long chatId) {
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
        cashedNoneShelteredAnimalsForChoosing.remove(animal);
        if (animal instanceof Cat cat) {
            cat.setSheltered(true);
            petOwner.setHasPets(true);
            cat.setPetOwner(petOwner);
            catsService.putPet(cat);
            petOwner.setLookingAboutPet(false);
            cache().getPetOwnersById().put(chatId,
                    petOwnersService.putPetOwner(petOwner));
            cache().getCatsByPetOwnerId().get(chatId).add(cat);
            startChoosingOneOfPets(chatId, ShelterType.CATS_SHELTER);
        } else if (animal instanceof Dog dog) {
            dog.setSheltered(true);
            petOwner.setHasPets(true);
            dog.setPetOwner(petOwner);
            dogsService.putPet(dog);
            petOwner.setLookingAboutPet(false);
            cache().getDogsByPetOwnerId().get(chatId).add(dog);
            cache().getPetOwnersById().put(chatId,
                    petOwnersService.putPetOwner(petOwner));
            startChoosingOneOfPets(chatId, ShelterType.DOGS_SHELTER);
        }
        return sender.sendResponse(new SendMessage(
                chatId, "Вы приютили питомца по имени *" + animal.getNickName() + "*"
        ).parseMode(ParseMode.Markdown));
    }
}
