package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(schema = "animals", name = "cats")
@NoArgsConstructor
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
}
