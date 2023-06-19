package com.telegrambotanimalshelter.listener.parts.requests;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.sendChatMessage;

@Component
public class VolunteerAndPetOwnerChat {

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private final MessageSender sender;

    public VolunteerAndPetOwnerChat(PetOwnersService petOwnersService, VolunteerService volunteerService, MessageSender sender) {
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.sender = sender;
    }

    public void startChat(Long id, String msg) {

        //вызов метода может происходить только со стороны усыновителя
        //поэтому находим сначала любого свободного волонтера
        Volunteer volunteer = volunteerService.findFreeVolunteer();

        //назначем поля усыновителю в базе данных и одновременно возвращаем его из метода
        PetOwner petOwner = petOwnersService.setPetOwnerToVolunteerChat(id, volunteer, true);

        //также назначаем поля волонтеру
        volunteer.setPetOwner(petOwner);
        volunteer.setFree(false);

        //и схораняем его в базу данных
        volunteerService.putVolunteer(volunteer);

        //отправляем сообщения волонтеру и усыновителю,
        // что они находятся в чате друг с другом
        sender.sendChatMessage(volunteer.getId(), msg);
        sender.sendChatMessage(petOwner.getId(), "С вами будет общаться волонтёр " + volunteer.getFirstName());
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

    public boolean checkPetOwnerChatStatus(Long petOwnerId){
        return petOwnersService.checkVolunteerChatStatus(petOwnerId);
    }

    public boolean checkVolunteer(Long volunteerId){
        return volunteerService.checkVolunteer(volunteerId);
    }

    private void sendToVolunteer(Long petOwnerId, String msg){
        PetOwner petOwner = petOwnersService.findPetOwner(petOwnerId);
        sender.sendMessage(petOwner.getVolunteer().getId(), msg);
    }

    private void sendToPetOwner(Long volunteerId, String msg){
        Volunteer volunteer = volunteerService.findVolunteer(volunteerId);
        sender.sendMessage(volunteer.getPetOwner().getId(), msg);
    }

    public boolean stopChat(Long petOwnerId, Long volunteerId, String msg) {
        if ("Прекратить чат".equals(msg)) {
            if (volunteerId == null) {

                //находим усыновителя в базе
                PetOwner petOwner = petOwnersService.findPetOwner(petOwnerId);

                //через усыновителя находим волонтера и применяем метод из сервиса
                Volunteer volunteer = volunteerService.setFree(petOwner.getVolunteer().getId(), true);

                //назначаем усыновителю новые значения полей
                petOwner.setVolunteerChat(false);
                petOwner.setVolunteer(null);

                //кладём усыновителя обратно в базу
                petOwnersService.putPetOwner(petOwner);
                sender.sendMessage(petOwner.getId(), "Вы закончили чат");
                sender.sendMessage(volunteer.getId(), "Вы закончили чат");

                sender.sendStartMessage(petOwner.getId());
                return true;
            }
            if (petOwnerId == null) {

                //находим волонтера в базе
                Volunteer volunteer = volunteerService.findVolunteer(volunteerId);

                //через волонтера находим усыновителя в базе и применяем метод из сервиса
                PetOwner petOwner = petOwnersService.setPetOwnerToVolunteerChat(volunteer.getPetOwner().getId(), null, false);

                //назначаем волонтеру новые значения полей
                volunteer.setPetOwner(null);
                volunteer.setFree(true);

                //кладём волонтера обратно в базу
                volunteerService.putVolunteer(volunteer);

                sender.sendMessage(volunteer.getId(), "Вы закончили чат");
                sender.sendMessage(petOwner.getId(), "Вы закончили чат");
                sender.sendStartMessage(petOwner.getId());
                return true;
            }
        }
        return false;
    }
}
