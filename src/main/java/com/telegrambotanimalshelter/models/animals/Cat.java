package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "animals", name = "cats")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cat extends Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private PetOwner petOwner;

    @OneToMany(mappedBy = "cat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CatReport> reports;

    @OneToMany(targetEntity = CatImage.class, mappedBy = "cat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CatImage> images;

    public Cat(String nickname, boolean isChipped, LocalDateTime registeredAt, PetOwner petOwner) {
        super(nickname, isChipped, registeredAt);
        this.petOwner = petOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cat cat)) return false;
        return id.equals(cat.id) && petOwner.equals(cat.petOwner) && reports.equals(cat.reports) && images.equals(cat.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, petOwner, reports, images);
    }
}
