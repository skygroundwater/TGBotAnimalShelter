package com.telegrambotanimalshelter.listener.parts.requests;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.listener.parts.keeper.Keeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

@Component
public class VolunteerAndPetOwnerChat<A extends Animal> {

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private final MessageSender<A> sender;

    private final Keeper keeper;

    public VolunteerAndPetOwnerChat(PetOwnersService petOwnersService, VolunteerService volunteerService, MessageSender<A> sender, Keeper keeper) {
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.sender = sender;
        this.keeper = keeper;
    }

    public boolean checkPetOwnerChatStatus(Long petOwnerId) {
        try {
            return keeper.getCashedPetOwners().get(petOwnerId).isVolunteerChat();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkVolunteer(Long volunteerId) {
        try {
            return !keeper.getCashedVolunteers().get(volunteerId).isFree();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void sendToVolunteer(Long petOwnerId, String msg) {
        sender.sendMessage(keeper.getCashedPetOwners().get(petOwnerId).getVolunteer().getId(), msg);
    }

    private void sendToPetOwner(Long volunteerId, String msg) {
        sender.sendMessage(keeper.getCashedVolunteers().get(volunteerId).getPetOwner().getId(), msg);
    }

    public void startChat(Long id, String msg) {

        //вызов метода может происходить только со стороны усыновителя
        //поэтому находим сначала любого свободного волонтера
        try {
            Volunteer volunteer = keeper.findFreeVolunteer();

            //назначем поля усыновителю в базе данных и одновременно возвращаем его из метода

            PetOwner petOwner = petOwnersService.setPetOwnerToVolunteerChat(id, volunteer, true);
            keeper.getCashedPetOwners().put(petOwner.getId(), petOwner);

            //также назначаем поля волонтеру
            volunteer.setPetOwner(petOwner);
            volunteer.setFree(false);

            //и схораняем его в базу данных
            keeper.getCashedVolunteers().put(volunteer.getId(), volunteerService.putVolunteer(volunteer));

            //отправляем сообщения волонтеру и усыновителю,
            // что они находятся в чате друг с другом
            sender.sendChatMessage(volunteer.getId(), msg);
            sender.sendChatMessage(petOwner.getId(), "С вами будет общаться волонтёр " + volunteer.getFirstName());
        } catch (NotFoundInDataBaseException e) {
            sender.sendMessage(id, e.getMessage());
        }
    }

    public void continueChat(Long petOwnerId, Long volunteerId, String msg) {

        //этот метод осуществляет одновременно и проверку и, в случае успешной проверки, завершает чат
        if (!stopChat(petOwnerId, volunteerId, msg)) {

            //в случае, если сообщение отправляет усыновитель
            if (petOwnerId != null && volunteerId == null) {
                sendToVolunteer(petOwnerId, msg);
            }

            //в случае, если сообщение отправил волонтёр
            if (volunteerId != null && petOwnerId == null) {
                sendToPetOwner(volunteerId, msg);
            }
        }
    }

    public boolean stopChat(Long petOwnerId, Long volunteerId, String msg) {
        if ("Прекратить чат".equals(msg)) {
            if (volunteerId == null) {

                //находим усыновителя в базе
                PetOwner petOwner = keeper.getCashedPetOwners().get(petOwnerId);

                //через усыновителя находим волонтера и применяем метод из сервиса
                Volunteer volunteer = keeper.getCashedVolunteers()
                        .put(petOwner.getVolunteer().getId(), volunteerService.setFree(petOwner.getVolunteer().getId(), true));

                //назначаем усыновителю новые значения полей
                petOwner.setVolunteerChat(false);
                petOwner.setVolunteer(null);

                //кладём усыновителя обратно в базу
                keeper.getCashedPetOwners().put(petOwner.getId(), petOwnersService.putPetOwner(petOwner));
                sender.sendMessage(petOwner.getId(), "Вы закончили чат");
                sender.sendMessage(volunteer.getId(), "Вы закончили чат");

                sender.sendStartMessage(petOwner.getId());
                return true;
            }
            if (petOwnerId == null) {

                //находим волонтера в базе
                Volunteer volunteer = keeper.getCashedVolunteers().get(volunteerId);

                //через волонтера находим усыновителя в базе и применяем метод из сервиса
                PetOwner petOwner = keeper.getCashedPetOwners()
                        .put(volunteer.getPetOwner().getId(), petOwnersService.setPetOwnerToVolunteerChat(
                                volunteer.getPetOwner().getId(), null, false));

                //назначаем волонтеру новые значения полей
                volunteer.setPetOwner(null);
                volunteer.setFree(true);

                //кладём волонтера обратно в базу
                keeper.getCashedVolunteers().put(volunteer.getId(), volunteerService.putVolunteer(volunteer));

                sender.sendMessage(volunteer.getId(), "Вы закончили чат");
                sender.sendMessage(petOwner.getId(), "Вы закончили чат");
                sender.sendStartMessage(petOwner.getId());
                return true;
            }
        }
        return false;
    }
}
