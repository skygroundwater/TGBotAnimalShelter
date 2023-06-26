package com.telegrambotanimalshelter.models.reports;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class Report {

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "diet")
    private String diet;

    @Column(name = "common_status")
    private String commonDescriptionOfStatus;

    @Column(name = "behavior")
    private String behavioralChanges;

    @Column(name="copied_owner_id")
    private Long copiedPetOwnerId;

    @Column(name = "copied_animal_id")
    private Long copiedAnimalId;

    @Column(name = "checked_by_volunteer")
    @ToString.Exclude
    private boolean checkedByVolunteer;

    @PrePersist
    private void init() {
        date = LocalDateTime.now().toLocalDate();
    }
}
