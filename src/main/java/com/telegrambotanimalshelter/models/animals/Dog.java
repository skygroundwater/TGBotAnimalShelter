package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dogs")
@Getter
@Setter
@NoArgsConstructor
public class Dog extends Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private PetOwner petOwner;

    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<DogReport> reports;

    public Dog(String nickname, boolean isChipped, LocalDateTime registeredAt, Shelter dogShelter, PetOwner petOwner) {
        super(nickname, isChipped, registeredAt, dogShelter);
        this.petOwner = petOwner;
    }
}
