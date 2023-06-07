package com.telegrambotanimalshelter.models.reports;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

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

}
