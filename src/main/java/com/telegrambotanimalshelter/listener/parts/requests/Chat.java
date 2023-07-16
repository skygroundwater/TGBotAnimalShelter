package com.telegrambotanimalshelter.listener.parts.requests;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteersService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

/**
 * Сущность, отвечающая за взаимодействие пользователя
 * с волонтером, посредством их обоюдного общением внутри телеграм бота
 *
 * @param <A>
 * @param <R>
 */
@Component
public class Chat<A extends Animal, R extends Report> {

    private final MessageSender<A> sender;

    private final CacheKeeper<A, R> keeper;

    public Chat(MessageSender<A> sender, CacheKeeper<A, R> keeper) {
        this.sender = sender;
        this.keeper = keeper;
    }

    private Cache<A, R> cache() {
        return keeper.getCache();
    }

    private VolunteersService volunteersService(){
        return keeper.getVolunteersService();
    }

    private PetOwnersService petOwnersService(){
        return keeper.getPetOwnersService();
    }

    /**
     * Метод проверяет является ли пользователь в данный
     * момент в активном статусе общения с волонтёром
     *
     * @param petOwnerId личный id усыновителя
     * @return true или false
     */
    public boolean checkPetOwnerChatStatus(Long petOwnerId) {
        PetOwner petOwner = cache().getPetOwnersById().get(petOwnerId);
        if (petOwner != null) {
            return petOwner.isVolunteerChat();
        } else return false;
    }

    /**
     * Метод помогает определить является ли волонтёр
     * на данный момент занятым перепиской с усыновителем
     *
     * @param volunteerId личный id волонтёра
     * @return true или false
     */
    public boolean checkVolunteer(Long volunteerId) {
        Volunteer volunteer = cache().getVolunteers().get(volunteerId);
        if (volunteer != null) {
            if (!volunteer.isInOffice() && !volunteer.isCheckingReports() && !volunteer.isFree()) {
                return true;
            } else return false;
        } else return false;
    }

    /**
     * Метод предоставляет усыновителю возможность начать
     * чат со свободным волонтером из кеша.
     *
     * @param chatId личный id пользователя
     * @param msg    текстовое сообщение от пользователя
     */
    public String startChat(Long chatId, String msg) {
        //вызов метода может происходить только со стороны усыновителя
        //поэтому находим сначала любого свободного волонтера
        if (checkUserForVolunteerStatus(chatId)) {
            try {
                Volunteer volunteer = keeper.findFreeVolunteer();
                //назначем поля усыновителю в базе данных и одновременно возвращаем его из метода
                PetOwner petOwner = petOwnersService()
                        .setPetOwnerToVolunteerChat(chatId, volunteer, true);
                cache().getPetOwnersById().put(petOwner.getId(), petOwner);
                //также назначаем поля волонтеру
                volunteer.setPetOwner(petOwner);
                volunteer.setFree(false);
                //и схораняем его в базу данных
                cache().getVolunteers().put(volunteer.getId(),
                        volunteersService().putVolunteer(volunteer));
                //отправляем сообщения волонтеру и усыновителю,
                // что они находятся в чате друг с другом
                String info = "С вами будет общаться волонтёр " + volunteer.getFirstName();
                sender.sendChatMessage(volunteer.getId(), msg);
                sender.sendChatMessage(petOwner.getId(), info);
                return info;
            } catch (NotFoundInDataBaseException e) {
                sender.sendMessage(chatId, e.getMessage());
                return e.getMessage();
            }
        } else {
            String info = "Вы сами являетесь волонтёром";
            sender.sendMessage(chatId, info);
            return info;
        }
    }

    public boolean checkUserForVolunteerStatus(Long chatId) {
        return cache().getVolunteers()
                .get(chatId) == null;
    }

    /**
     * Метод предоставляет возможность остановить переписку
     * и со стороны волонтера и со стороны усыновителя
     *
     * @param petOwnerId  личный id усыновителя
     * @param volunteerId личный id волонтёра
     * @param msg         текстовое сообщение
     * @return true или false
     */
    public boolean stopChat(Long petOwnerId, Long volunteerId, String msg) {
        if ("Прекратить чат".equals(msg)) {
            if (volunteerId == null) {
                //находим усыновителя в базе
                PetOwner petOwner = cache().getPetOwnersById().get(petOwnerId);
                //через усыновителя находим волонтера и применяем метод из сервиса
                Volunteer volunteer = cache().getVolunteers()
                        .put(petOwner.getVolunteer().getId(),
                                volunteersService().setFree(
                                        petOwner.getVolunteer().getId(), true));
                //назначаем усыновителю новые значения полей
                petOwner.setVolunteerChat(false);
                petOwner.setVolunteer(null);
                //кладём усыновителя обратно в базу
                cache().getPetOwnersById().put(petOwner.getId(),
                        petOwnersService().putPetOwner(petOwner));
                sender.sendMessage(petOwner.getId(), "Вы закончили чат");
                sender.sendMessage(volunteer.getId(), "Вы закончили чат");
                sender.sendStartMessage(petOwner.getId());
                return true;
            }
            if (petOwnerId == null) {
                //находим волонтера в базе
                Volunteer volunteer = cache().getVolunteers().get(volunteerId);
                //через волонтера находим усыновителя в базе и применяем метод из сервиса
                PetOwner petOwner = cache().getPetOwnersById()
                        .put(volunteer.getPetOwner().getId(), petOwnersService()
                                .setPetOwnerToVolunteerChat(
                                volunteer.getPetOwner().getId(), null, false));
                //назначаем волонтеру новые значения полей
                volunteer.setPetOwner(null);
                volunteer.setFree(true);
                //кладём волонтера обратно в базу
                cache().getVolunteers().put(volunteer.getId(),
                        volunteersService().putVolunteer(volunteer));
                sender.sendMessage(volunteer.getId(), "Вы закончили чат");
                sender.sendMessage(petOwner.getId(), "Вы закончили чат");
                sender.sendStartMessage(petOwner.getId());
                return true;
            }
        }
        return false;
    }

    /**
     * Метод позволяет продолжать переписку волонтёра
     * и усыновителя, пока не поступит команда об остановке переписки
     *
     * @param petOwnerId  личный id усыновителя
     * @param volunteerId личный id волонтёра
     * @param msg         текстовое сообщение
     */
    public boolean continueChat(Long petOwnerId, Long volunteerId, String msg) {
        //этот метод осуществляет одновременно и проверку и, в случае успешной проверки, завершает чат
        if (!stopChat(petOwnerId, volunteerId, msg)) {
            //в случае, если сообщение отправляет усыновитель
            if (petOwnerId != null && volunteerId == null) {
                sendToVolunteer(petOwnerId, msg);
                return true;
            }
            //в случае, если сообщение отправил волонтёр
            if (volunteerId != null && petOwnerId == null) {
                sendToPetOwner(volunteerId, msg);
                return true;
            }
        }
        return false;
    }

    /**
     * Отправляет сообщение усыновителю от волонтера
     *
     * @param petOwnerId личный id усыновителя
     * @param msg        текстовое сообщение от волонтера
     * @hidden все данные о волонтере берутся из кеша
     */
    private void sendToVolunteer(Long petOwnerId, String msg) {
        Volunteer volunteer = cache().getPetOwnersById().get(petOwnerId).getVolunteer();
        if (volunteer != null) {
            sender.sendMessage(volunteer.getId(), msg);
        }
    }

    /**
     * Отправляет сообщение от волонтера к усыновителю
     *
     * @param volunteerId личный id усыновителя
     * @param msg         текстовое сообщение от волонтера
     * @hidden все данные о об усыновителе берутся из кеша
     */
    private void sendToPetOwner(Long volunteerId, String msg) {
        PetOwner petOwner = cache().getVolunteers().get(volunteerId).getPetOwner();
        if (petOwner != null) {
            sender.sendMessage(petOwner.getId(), msg);
        }
    }
}
