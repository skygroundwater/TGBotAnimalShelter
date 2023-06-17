package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.TelegramBot;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import org.springframework.stereotype.Component;

import static com.telegrambotanimalshelter.utils.Constants.sendMessage;

@Component
public class VolunteerAndPetOwnerChat {

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private final MessageSender sender;

    private final TelegramBot telegramBot;

    public VolunteerAndPetOwnerChat(PetOwnersService petOwnersService, VolunteerService volunteerService, MessageSender sender, TelegramBot telegramBot) {
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.sender = sender;
        this.telegramBot = telegramBot;
    }

    public void startChatWithVolunteer(Long id, String msg) {
        Volunteer volunteer = volunteerService.findFreeVolunteer();

        PetOwner petOwner = petOwnersService.setPetOwnerToVolunteerChat(id, volunteer, true);

        volunteer.setPetOwner(petOwner);

        volunteer.setFree(false);

        volunteerService.putVolunteer(volunteer);

        sendMessage(sender, volunteer.getId(), msg);

        sendMessage(sender, petOwner.getId(), "С вами будет общаться волонтёр " + volunteer.getFirstName());
    }

    public void volunteerAndPetOwnerChat(Long petOwnerId, Long volunteerId, String msg) {
        if (petOwnerId == null) {
            Volunteer volunteer = volunteerService.findVolunteer(volunteerId);
            sendMessage(sender, volunteer.getPetOwner().getId(), msg);
        } else if (volunteerId == null) {
            PetOwner petOwner = petOwnersService.findPetOwner(petOwnerId);
            sendMessage(sender, petOwner.getVolunteer().getId(), msg);
        }
    }




}
