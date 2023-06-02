package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cats")
@NoArgsConstructor
@Getter
@Setter
public class Cat extends Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "petowner_id")
    private PetOwner petOwner;

    @Column(name = "shelter")
    private String shelter;

    public Cat(String nickname, boolean isChipped, LocalDateTime registeredAt, Shelter shelter, PetOwner petOwner) {
        super(nickname, isChipped, registeredAt);
        this.shelter = shelter.getShelterType().name();
        this.petOwner = petOwner;
    }
}
