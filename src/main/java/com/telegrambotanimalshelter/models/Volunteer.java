package com.telegrambotanimalshelter.models;

import com.telegrambotanimalshelter.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Table(schema = "public", name = "volunteers")
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class Volunteer {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @Column(name = "is_free")
    @Builder.Default
    private boolean isFree = true;

    @Column(name = "checking_reports")
    @Builder.Default
    private boolean checkingReports = false;

    @Column(name = "in_office")
    @Builder.Default
    private boolean inOffice = false;

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
    @Builder.Default
    private boolean nonExpired = true;

    @Column(name = "non_locked")
    @Builder.Default
    private boolean nonLocked = true;

    @Column(name = "non_credentials_expired")
    @Builder.Default
    private boolean nonCredentialsExpired = true;

    @Column(name = "is_enabled")
    @Builder.Default
    private boolean isEnabled = true;


    public Volunteer(Long id, String firstName,
                     String lastName, String userName, PetOwner petOwner) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.petOwner = petOwner;
        this.inOffice = false;
        this.checkingReports = false;
    }

    public Volunteer(Long id, String firstName,
                     String lastName, String userName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.inOffice = false;
        this.checkingReports = false;
    }
}
