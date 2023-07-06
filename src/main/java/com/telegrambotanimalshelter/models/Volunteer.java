package com.telegrambotanimalshelter.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(schema = "public", name = "volunteers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Volunteer {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "link")
    private String link;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @Column(name = "is_free")
    private boolean isFree;

    @Column(name = "checking_reports")
    private boolean checkingReports;

    @Column(name = "in_office")
    private boolean inOffice;

    @OneToOne
    @JoinColumn(name = "petowner_id", referencedColumnName = "id")
    private PetOwner petOwner;



}
