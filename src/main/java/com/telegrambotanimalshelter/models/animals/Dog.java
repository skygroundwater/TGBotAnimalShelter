package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.DogReport;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "animals", name = "dogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dog extends Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private PetOwner petOwner;

    @OneToMany(mappedBy = "dog", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<DogReport> reports;

    @OneToMany(targetEntity = DogImage.class, mappedBy = "dog", fetch = FetchType.EAGER)
    private List<DogImage> images;

    public Dog(String nickname, boolean sheltered, LocalDateTime registeredAt, PetOwner petOwner, String about, byte[] photo) {
        super(nickname, sheltered, registeredAt, about, photo);
        this.petOwner = petOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dog dog)) return false;
        return id.equals(dog.id) && petOwner.equals(dog.petOwner) && reports.equals(dog.reports) && images.equals(dog.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reports, images);
    }
}
