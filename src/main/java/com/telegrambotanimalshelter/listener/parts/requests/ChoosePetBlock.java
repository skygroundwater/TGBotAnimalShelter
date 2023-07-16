package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petphotoservice.PetPhotoService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChoosePetBlock<A extends Animal, R extends Report> {
    private final MessageSender<A> sender;

    private final CacheKeeper<A, R> keeper;

    private final PetPhotoService petPhotoService;

    private ConcurrentMap<Long, List<A>> cashedNoneShelteredAnimalsForChoosing;

    private ConcurrentMap<Long, A> showingAnimalsByPetOwnerID;

    public ChoosePetBlock(MessageSender<A> sender,
                          CacheKeeper<A, R> keeper,
                          PetPhotoService petPhotoService) {
        this.sender = sender;
        this.keeper = keeper;
        this.petPhotoService = petPhotoService;
        this.cashedNoneShelteredAnimalsForChoosing = new ConcurrentHashMap<>();
        this.showingAnimalsByPetOwnerID = new ConcurrentHashMap<>();
    }

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    public SendResponse startChoosingOneOfPets(Long chatId, ShelterType shelterType) {
        cache().getPetOwnersById().put(chatId,
                keeper.getPetOwnersService()
                        .setChoosingPets(chatId, true));
        ReplyKeyboardMarkup replyMarkup
                = new ReplyKeyboardMarkup("Не выбирать животное")
                .oneTimeKeyboard(true);
        cashedNoneShelteredAnimalsForChoosing.put(chatId, new ArrayList<>());
        switch (shelterType) {
            case DOGS_SHELTER -> cache().getCachedAnimals().stream().filter(
                    animal -> animal instanceof Dog && !animal.isSheltered()).forEach(
                    dog -> {
                        replyMarkup.addRow(dog.getNickName());
                        cashedNoneShelteredAnimalsForChoosing.get(chatId).add(dog);
                    });
            case CATS_SHELTER -> cache().getCachedAnimals().stream().filter(
                    animal -> animal instanceof Cat && !animal.isSheltered()).forEach(
                    cat -> {
                        replyMarkup.addRow(cat.getNickName());
                        cashedNoneShelteredAnimalsForChoosing.get(chatId).add(cat);
                    });
        }
        return sender.sendResponse(new SendMessage(chatId, "*Вы можете выбрать животное из списка*")
                .parseMode(ParseMode.Markdown).replyMarkup(replyMarkup));
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
                        keeper.getPetOwnersService().setLookingAboutPet(chatId, true));
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
                keeper.getPetOwnersService()
                        .setChoosingPets(chatId, false));
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
        Dog dog = keeper.getDogService().findPetByName(name);
        if (dog == null) {
            sender.sendResponse(new SendMessage(chatId,
                    "Собаки с таким именем нет в базе или задан неверный запрос"));
            throw new NotFoundInDataBaseException("Собаки с таким именем нет в базе или задан неверный запрос");
        } else {
            return dog;
        }
    }

    public Cat getCatByNameFromUserRequest(String name, Long chatId) {
        Cat cat = keeper.getCatService().findPetByName(name);
        if (cat == null) {
            sender.sendResponse(new SendMessage(chatId, "Кошки с таким именем нет в базе или задан неверный запрос"));
            throw new NotFoundInDataBaseException("Кошки с таким именем нет в базе или задан неверный запрос");
        } else {
            return cat;
        }
    }

    public SendResponse getAnimalInfo(Animal animal, Long chatId) {
        SendMessage sendMessage;
        if (animal != null) {
            sendMessage = new SendMessage(chatId,
                    Objects.requireNonNullElse(animal.getAbout(), "Информация отсутствует"));
        } else {
            sendMessage = new SendMessage(chatId,
                    "Повторите запрос или свяжитесь с волонтером");
        }
        return sender.sendResponse(sendMessage);
    }

    public SendResponse getPetPhotoFromShelter(Animal animal, Long chatId) {
        File petPhoto = petPhotoService.getPetPhoto(animal, animal.getNickName());
        if (petPhoto != null) {
            return sender.sendResponse(new SendPhoto(chatId, petPhoto));
        } else {
            return sender.sendResponse(new SendMessage(chatId, "Фото отсутствует"));
        }
    }

    public SendResponse getPetFromShelter(A animal, Long chatId) {
        cashedNoneShelteredAnimalsForChoosing.get(chatId).remove(animal);
        cache().getPetOwnersById().forEach((aLong, petOwner) -> {
                    if (aLong.equals(chatId)) {
                        petOwner.setHasPets(true);
                        petOwner.setLookingAboutPet(false);
                        if (animal instanceof Cat cat) {
                            cat.setSheltered(true);
                            cat.setPetOwner(petOwner);
                            keeper.getCatService().putPet(cat);
                            cache().getCatsByPetOwnerId().get(chatId).add(cat);
                            startChoosingOneOfPets(chatId, ShelterType.CATS_SHELTER);
                        } else if (animal instanceof Dog dog) {
                            dog.setSheltered(true);
                            dog.setPetOwner(petOwner);
                            keeper.getDogService().putPet(dog);
                            cache().getDogsByPetOwnerId().get(chatId).add(dog);
                            startChoosingOneOfPets(chatId, ShelterType.CATS_SHELTER);
                        }}});
        return sender.sendResponse(new SendMessage(
                chatId, "Вы приютили питомца по имени *" + animal.getNickName() + "*"
        ).parseMode(ParseMode.Markdown));
    }
}
