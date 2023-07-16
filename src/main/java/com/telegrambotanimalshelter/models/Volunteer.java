package com.telegrambotanimalshelter.models;

import com.telegrambotanimalshelter.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(schema = "public", name = "volunteers")
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

    @NotEmpty(message = "password should be filled")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "non_expired")
    private boolean nonExpired;

    @Column(name = "non_locked")
    private boolean nonLocked;

    @Column(name = "non_credentials_expired")
    private boolean nonCredentialsExpired;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    public Volunteer(Long id, String link,  String firstName,
                     String lastName, String userName,
                     boolean isFree, boolean inOffice,
                     boolean isCheckingReports, PetOwner petOwner){
        this.id = id;
        this.link = link;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.isFree = isFree;
        this.inOffice = inOffice;
        this.checkingReports = isCheckingReports;
        this.petOwner = petOwner;
    }

    public Volunteer(){
        nonCredentialsExpired = true;
        nonExpired = true;
        nonLocked = true;
        isEnabled = true;
        inOffice = false;
        isFree = true;
        checkingReports = false;
    }

}
