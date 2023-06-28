package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.DogReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(schema = "animals", name = "dogs")
@Getter
@Setter
@NoArgsConstructor
public class Dog extends Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "about_dog")
    private String aboutDog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private PetOwner petOwner;

    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<DogReport> reports;

    @OneToMany(targetEntity = DogImage.class, mappedBy = "dog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DogImage> images;

    public Dog(String nickname, boolean isChipped, LocalDateTime registeredAt, PetOwner petOwner) {
        super(nickname, isChipped, registeredAt);
        this.petOwner = petOwner;
    }
}
