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

    public Cat(String nickname, boolean sheltered, LocalDateTime registeredAt, PetOwner petOwner, String about, byte[] photo) {
        super(nickname, sheltered, registeredAt, about, photo);
        this.petOwner = petOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cat cat = (Cat) o;
        return Objects.equals(id, cat.id) && Objects.equals(petOwner, cat.petOwner) && Objects.equals(reports, cat.reports) && Objects.equals(images, cat.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reports, images);
    }
}
