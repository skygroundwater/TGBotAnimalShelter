package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.FileService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Сущность, отвечающая за взаимодействие с пользователем
 * на этапе записи отчета о его питомце.
 *
 * @param <A>
 * @param <R>
 * @param <I>
 */
@Component
public class ReportBlock<A extends Animal, R extends Report, I extends AppImage> {

    private final MessageSender<A> sender;

    private final FileService<I> fileService;

    private final CacheKeeper<A, R> keeper;

    private HashMap<Long, ArrayList<A>> cashedNoneReportedPetNames;

    public ReportBlock(MessageSender<A> sender,
                       FileService<I> fileService,
                       CacheKeeper<A, R> keeper) {
        this.sender = sender;
        this.fileService = fileService;
        this.keeper = keeper;
        this.cashedNoneReportedPetNames = new HashMap<>();
    }

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    /**
     * Работает с данными, поступающими от пользователя
     * в момент его активности на предмет отчета о питомце,
     * фильтруя все отправляемые им данные.
     *
     * @param chatId  личный id пользователя
     * @param message сообщение от пользователя
     */
    public void reportFromPetOwnerBlock(Long chatId, Message message) {
        /*
         если проверка прошла успешно, то предлагаем пользователю
         отправить информацию о питании питомца в данный период времени
        */
        if (checkIsMessageAPhoto(chatId, message)) {
            sendMessageToTakeDiet(chatId);
        } else
        /*
        просим отправить фотографию животного,
        если проверка прошла успешно
        */
            if (checkIsMessageANameOfPet(chatId, message)) {
                sendMessageToTakePhoto(chatId);
            } else if (message.text() != null) {
        /*
        отрезаем от сообщения префикс и проверяем его
         */
                String text = message.text();
                String preFix = text.split(" ")[0];
                String info = text.substring(preFix.length());
                if (!text.equals("Прервать отчет")) {
                    switch (preFix) {
                        case "Диета:" -> {
                            sendMessageToTakeCommonStatus(chatId, info);
                        }
                        case "Состояние:" -> {
                            sendMessageToTakeChanges(chatId, info);
                        }
                        case "Изменения:" -> {
                            stopReport(chatId, info);
                        }
                        case "/break" -> {
                            forcedStopReport(chatId);
                        }
                        default -> sendWarningLetter(chatId);
                    }
                } else forcedStopReport(chatId);
            }
    }

    /**
     * Метод, предлагающий выбор пользователю того,
     * для какого из имеющихся у него питомцев, взятых
     * из приюта он желает отправить отчет.
     *
     * @param chatId личный id пользователя
     */
    private void chooseAnyPetMessages(Long chatId) {
        /*
        создаем сущность сообщения
         */
        SendMessage sendMessage = new SendMessage(chatId,
                "Выберите животное, на которого хотите отправить отчет по его кличке");
        /*
        задаем клавиатуру
         */
        ReplyKeyboardMarkup choosePetMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("Прервать отчет")).oneTimeKeyboard(true);
        /*
        кладем в закешированную мапу питомцев пользователя,
        которые еще пока не отрапартованы,
        ключом выступает личный id пользователя, а значением - коллекция с животными
         */
        cashedNoneReportedPetNames.put(chatId, new ArrayList<>());
        /*
        берем из держателя кеша котов и собак пользователя
        и добавляем их имена к клавиатуре, предоставляя выбор
         */
        for (Cat cat : cache().getCatsByPetOwnerId().get(chatId)) {
            if (!cat.isReported()) {
                choosePetMarkup.addRow(cat.getNickName());
                // сохраняем кошку в кеше
                cashedNoneReportedPetNames.get(chatId).add((A) cat);
            }
        }
        for (Dog dog : cache().getDogsByPetOwnerId().get(chatId)) {
            if (!dog.isReported()) {
                choosePetMarkup.addRow(dog.getNickName());
                // сохраняем собаку в кеше
                cashedNoneReportedPetNames.get(chatId).add((A) dog);
            }
        }
        /*
         задаем отправляемому сообщению
         клавиатуру после ее структуризации
        */
        sendMessage.replyMarkup(choosePetMarkup);
        sender.sendResponse(sendMessage);
    }

    /**
     * В этом методе решается, начинать работу с
     * пользователем по отправке отчета или нет.
     *
     * @param chatId личный id пользователя
     * @hidden Если у пользователя нет животных,
     * то ему об этом говорится в обратно сообщении.
     * Если будут, то переменной reportRequest
     * присваивается значение true и отправляется
     * в кеш и в базу данных.
     */
    public PetOwner startReportFromPetOwner(Long chatId) {
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
        if (petOwner != null && petOwner.isHasPets()) {
            petOwner = cache().getPetOwnersById().put(chatId,
                    keeper.getPetOwnersService().setPetOwnerReportRequest(chatId, true));
            chooseAnyPetMessages(chatId);
        } else sender.sendMessage(chatId, "У вас нет животных");
        return petOwner;
    }

    /**
     * Этот метод проверяет, какую именно кличку
     * одного из питомцев выбрал пользователь.
     * И по этому конкретному животному
     * инициализируем отчёт
     *
     * @param chatId  личный id пользователя
     * @param message сообщение от пользователя
     * @return true или false
     * @hidden выбор происходит из тех животных,
     * которых мы предварительно закешировали
     * в локальной мапе.
     */
    public boolean checkIsMessageANameOfPet(Long chatId, Message message) {
        for (A animal : cashedNoneReportedPetNames.get(chatId)) {
            if (animal.getNickName().equals(message.text())) {
                //в кеше создаем сущность отчета и наполнять
                //его будем по мере прохождения этапов блока
                createReportForAnimal(chatId, animal);
                return true;
            }
        }
        return false;
    }


    public PetOwner createReportForAnimal(Long chatId, A animal) {
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
        if (animal instanceof Cat cat) {
            cache().getActualReportByPetOwnerId().put(chatId,
                    (R) keeper.getCatReportService().putReport(
                            CatReport.builder().cat(cat)
                                    .petOwner(petOwner)
                                    .images(new ArrayList<>())
                                    .build()));
            cache().getActualPetsInReportProcess().put(chatId, animal);
        } else if (animal instanceof Dog dog) {
            cache().getActualReportByPetOwnerId().put(chatId,
                    (R) keeper.getDogReportService().putReport(
                            DogReport.builder().dog(dog)
                                    .petOwner(petOwner)
                                    .images(new ArrayList<>())
                                    .build()));
            cache().getActualPetsInReportProcess().put(chatId, animal);
        }
        return petOwner;
    }

    /**
     * Проверяет, является ли полученное сообщение от
     * пользователя фотографией. Если является, то
     * сразу применяет его к созданной сущности отчета
     * в кеше. Если нет то, отправляет ответ пользователю.
     *
     * @param chatId  личный id пользователя
     * @param message сообщение от ползователя
     * @return true или false
     */
    public boolean checkIsMessageAPhoto(Long chatId, Message message) {
        if (message.photo() != null || message.document() != null) {
            /*
            берем активную сущность отчета пользователя из кеша
             */
            R report = cache().getActualReportByPetOwnerId().get(chatId);
            report.setCopiedPetOwnerId(chatId);
            /*
            осуществляем проверку сущности отчета на соответствие
            отчета для кота или же для собаки.
            далее производим манипуляции по его наполнению
             */
            if (report instanceof CatReport catReport) {
                catReport.getImages().add(
                        (CatImage) fileService.processDoc(
                                new CatImage(catReport.getId()), message));
                return true;
            } else if (report instanceof DogReport dogReport) {
                dogReport.getImages().add(
                        (DogImage) fileService.processDoc(
                                new DogImage(dogReport.getId()), message));
                return true;
            }
        }
        return false;
    }

    /**
     * Метод осуществляет проверку на то, есть ли у пользователя
     * на данный момент активный статус отправки отчета в кеше
     *
     * @param chatId личный id пользователя
     * @return true или false
     */
    public boolean checkReportRequestStatus(Long chatId) {
        PetOwner petOwner = cache().getPetOwnersById().get(chatId);
        if (petOwner != null) {
            return petOwner.isReportRequest();
        } else return false;
    }

    /**
     * Метод применяется только в том случае, когда
     * пользователь решил <b>преждевременно остановить</b>
     * процесс отправки отчета по своему питомцу
     *
     * @param chatId Личный id пользователя
     */
    private void forcedStopReport(Long chatId) {
        sender.sendMessage(chatId, "Вы прервали отправку отчета. Пожалуйста, не забудьте отправить его позже.");
        R report = cache().getActualReportByPetOwnerId().remove(chatId);
        if (report instanceof CatReport catReport) {
            keeper.getCatReportService().deleteReportById(catReport.getId());
        } else if (report instanceof DogReport dogReport) {
            keeper.getDogReportService().deleteReportById(dogReport.getId());
        }
        breakReport(chatId);
    }

    private void breakReport(Long chatId) {
        cache().getPetOwnersById().put(chatId,
                keeper.getPetOwnersService()
                        .setPetOwnerReportRequest(chatId, false));
    }

    /**
     * Метод применяется, когда отправка отчета
     * подошла к своему логическому завершению
     * Сохраняет отчет в базу данных, с привязкой
     * его к животному. Также привязывает животное к отчёту.
     *
     * @param chatId            личный id пользователя
     * @param behavioralChanges сообщение от пользователя, описывающее
     *                          какие изменения произошли с питомцем
     */
    private void stopReport(Long chatId, String behavioralChanges) {
        //получаем отчет из кеша и заполняем в нем переменную behavioralChanges
        /*
        производим проверку соответствия отчета к определенному классу
        и производим манипуляции с данными для привязок и сохранения в базу данных
         */
        cache().getActualReportByPetOwnerId().computeIfPresent(chatId, (aLong, rpt) -> {
            rpt.setBehavioralChanges(behavioralChanges);
            A animal = cache().getActualPetsInReportProcess().remove(chatId);
            animal.setReported(true);
            if (rpt instanceof CatReport catReport) {
                catReport.getImages().stream().findFirst().ifPresent(image -> {
                    animal.setPhoto(image.getFileAsArrayOfBytes());
                    Cat cat = keeper.getCatService().putPet((Cat) animal);
                    catReport.setCopiedAnimalId(cat.getId());
                    image.setCat(cat);
                    image.setCatReport(catReport);
                    cache().getCatImages().add(image);
                    cache().getCachedReports().add(
                            (R) keeper.getCatReportService().putReport(catReport));
                });
            } else if (rpt instanceof DogReport dogReport) {
                Dog dog = keeper.getDogService().putPet((Dog) animal);
                dogReport.getImages().stream().findFirst().ifPresent(image -> {
                    animal.setPhoto(image.getFileAsArrayOfBytes());
                    dogReport.setCopiedAnimalId(dog.getId());
                    image.setDog(dog);
                    image.setDogReport(dogReport);
                    cache().getDogImages().add(image);
                    cache().getCachedReports().add(
                            (R) keeper.getDogReportService().putReport(dogReport));
                });
            }
            return rpt;
        });
        //отправляем ответ и обновляем данные о пользователе в кеше и в базе данных
        sender.sendMessage(chatId, "Спасибо Вам за ваш отчет. Если будет что-то не так - волонтёр отпишетися вам. Желаем удачи.");
        breakReport(chatId);
    }

    /**
     * Метод предлагает пользователю последующим сообщением
     * отправить актуальную фотографию своего питомца
     *
     * @param chatId личный id пользователя
     */
    public void sendMessageToTakePhoto(Long chatId) {
        sender.sendMessage(chatId, """
                Отлично, вы начали отправлять запрос.
                Следующим сообщением приложите, пожалуйста актуальное*фото* питомца.
                 Пожалуйста, позаботьтесь о хорошем освещении при фотографировании""");
    }

    /**
     * Метод предлагает пользователю последующим сообщением
     * отправить информацию том, какое питание у питомца
     *
     * @param chatId личный id пользователя
     */
    private void sendMessageToTakeDiet(Long chatId) {
        sender.sendMessage(chatId, """
                Отлично. Теперь отправьте сообщением повседневный рацион вашего животного. Префикс *Диета: *""");
    }

    /**
     * Метод принимает информацию от пользователя о диете
     * питомца. И предлагает последующим сообщением описать
     * общее состояние животного.
     *
     * @param chatId личный id пользователя
     * @param diet   сообщение от пользователя,
     *               описывающее диету собаки
     * @hidden также метод сохраняет эти данные в кеше
     */
    private void sendMessageToTakeCommonStatus(Long chatId, String diet) {
        sender.sendMessage(chatId, "Мы уже близки к завершению. Поделитесь общим состоянием животного.\n" +
                " Как его самочувствие и процесс привыкания к новому месту? Префикс *Состояние: *");
        cache().getActualReportByPetOwnerId().get(chatId).setDiet(diet);
    }

    /**
     * Метод принимает информацию от пользователя об общем
     * состоянии питомца. И предлагает последующим сообщением
     * описать изменения, которые произошли с животным
     *
     * @param chatId       личный id пользователя
     * @param commonStatus сообщение от пользователя, описывающее
     *                     общее состояние питомца
     * @hidden также метод сохраняет данные в кеше
     */
    private void sendMessageToTakeChanges(Long chatId, String commonStatus) {
        sender.sendMessage(chatId, """
                Последняя наша просьба - поделиться процессом изменения животного.
                Как идет процесс восчпитания? Может быть, животное стало проявлять
                 новые черты в своем поведении? Префикс *Изменения: *
                """);
        cache().getActualReportByPetOwnerId().get(chatId).setCommonDescriptionOfStatus(commonStatus);
    }

    /**
     * Метод отправляет предупреждение о том, что
     * пользователь не соблюдает алгоритм заполнения отчета.
     *
     * @param chatId личный id пользователя
     */
    private void sendWarningLetter(Long chatId) {
        sender.sendMessage(chatId, """
                Дорогой усыновитель, вы отклонились от алгоритма заполнения отчета.
                """);
    }
}