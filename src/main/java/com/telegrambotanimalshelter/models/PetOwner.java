package com.telegrambotanimalshelter.models;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "public", name = "petowners")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PetOwner {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "has_pets")
    private boolean hasPets;

    @Column(name = "contact_request_chat")
    private boolean contactRequest;

    @Column(name = "report_request_chat")
    private boolean reportRequest;

    @Column(name="choosing_pet")
    private boolean choosingPet;

    @Column(name = "looking_about_pet")
    private boolean lookingAboutPet;

    @Column(name = "volunteer_chat")
    private boolean volunteerChat;

    @Column(name = "is_registering")
    private boolean isRegistering;

    @OneToOne
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
    private Volunteer volunteer;

    @OneToMany(mappedBy = "petOwner", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CatReport> catReports;

    @OneToMany(mappedBy = "petOwner", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<DogReport> dogReports;

    @OneToMany(mappedBy = "petOwner")
    @ToString.Exclude
    private List<Cat> cats;

    @OneToMany(mappedBy = "petOwner")
    @ToString.Exclude
    private List<Dog> dogs;

    public PetOwner(Long id, String firstName,
                    String lastName, String userName,
                    LocalDateTime registeredAt, boolean hasPets) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registeredAt = registeredAt;
        this.userName = userName;
        this.hasPets = hasPets;
        this.contactRequest = false;
        this.reportRequest = false;
        this.volunteerChat = false;
        this.isRegistering = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetOwner petOwner = (PetOwner) o;
        return hasPets == petOwner.hasPets && contactRequest == petOwner.contactRequest && reportRequest == petOwner.reportRequest && volunteerChat == petOwner.volunteerChat && Objects.equals(id, petOwner.id) && Objects.equals(firstName, petOwner.firstName) && Objects.equals(lastName, petOwner.lastName) && Objects.equals(userName, petOwner.userName) && Objects.equals(registeredAt, petOwner.registeredAt) && Objects.equals(phoneNumber, petOwner.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, userName, registeredAt, phoneNumber, hasPets, contactRequest, reportRequest, volunteerChat, volunteer);
    }
}
