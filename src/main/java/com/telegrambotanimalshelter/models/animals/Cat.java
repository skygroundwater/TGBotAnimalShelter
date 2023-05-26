package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import jakarta.persistence.*;
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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "petowner_id")
    private PetOwner petOwner;

    public Cat(String nickname, boolean isChipped, LocalDateTime registeredAt, Shelter shelter, PetOwner petOwner) {
        super(nickname, isChipped, registeredAt, shelter);
        this.petOwner = petOwner;
    }
}
