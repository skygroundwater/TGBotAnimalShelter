package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChoosePetForPotentialOwnerBlock<A extends Animal, R extends Report> {


    private final MessageSender<A> sender;
    private final CatsRepository catsRepository;

    private final DogsRepository dogsRepository;

    private final CacheKeeper<A, R> cacheKeeper;

    public ChoosePetForPotentialOwnerBlock(MessageSender<A> sender,
                                           CatsRepository catsRepository,
                                           DogsRepository dogsRepository, CacheKeeper<A, R> cacheKeeper) {
        this.sender = sender;
        this.catsRepository = catsRepository;
        this.dogsRepository = dogsRepository;
        this.cacheKeeper = cacheKeeper;
    }

    public List<Cat> getAllNotShelteredCats() {
        return catsRepository.findCatsBySheltered(false);
    }

    public List<Dog> getAllNotShelteredDogs() {
        return dogsRepository.findDogsBySheltered(false);
    }

    /**
     * Отправляет пользователю информацию о всех животных в приюте
     *
     * @param chatId
     * @param callbackQuery
     */
    public boolean sendNotShelteredAnimals(Long chatId, CallbackQuery callbackQuery) {
        StringBuilder builder = new StringBuilder();
        boolean flag = false;


        if (callbackQuery.data().equals("_get_cat")) {
            List<Cat> allNotShelteredCats = getAllNotShelteredCats();
            if (allNotShelteredCats.size() == 0) {
                builder.append("На данный момент все животные нашли своих хозяев :)\n");
            } else {
                flag = true;
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
                flag = true;
                builder.append("В нашем приюте проживают:\n");
                for (Dog dogs : allNotShelteredDogs) {
                    builder.append(dogs.getNickName()).append("\n");
                }
                builder.append("Информацию о каком животном вы бы хотели посмотреть?\n");
            }
        }
        sender.sendMessage(chatId, builder.toString());
        return flag;
    }

    // todo написать метод для поиска питомца после ввода имени в чате (только после вывода списка доступных в приюте животных)
//    public Animal getAnimalByNameFromUserRequest(Message message, CallbackQuery callbackQuery) {
//
//        String nameInput = message.text().trim();
//
//        if (callbackQuery.data().equals("_get_cat")) {
//            Cat cat = catsRepository.findCatsByNickName(nameInput);
//            if (cat == null) {
//                throw new NotFoundInDataBaseException("Кошки с таким именем нет в базе или задан неверный запрос");
//            } else {
//                getAnimalInfoMarkup(cat);
//                return cat;
//            }
//        } else if (callbackQuery.data().equals("_get_dog")) {
//            Dog dog = dogsRepository.findDogsByNickName(nameInput);
//            if (dog == null) {
//                throw new NotFoundInDataBaseException("Собаки с таким именем нет в базе или задан неверный запрос");
//            } else {
//                getAnimalInfoMarkup(dog);
//                return dog;
//            }
//        } else
//            throw new NotFoundInDataBaseException("Животного с таким именем нет в базе или задан неверный запрос");
//    }

    private InlineKeyboardMarkup getAnimalInfoMarkup(Animal animal) {

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Посмотреть информацию о будущем питомце: " + animal.getNickName())
                        .callbackData("_animal_info"),
                new InlineKeyboardButton("Посмотреть фото будущего питомца")
                        .callbackData("_animal_photo"),
                new InlineKeyboardButton("Приютить животное")
                        .callbackData("_animal_approve"),
                new InlineKeyboardButton("Назад к выбору приюта")
                        .callbackData("back")
        );
    }

}
