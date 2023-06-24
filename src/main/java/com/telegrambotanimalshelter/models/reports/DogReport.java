package com.telegrambotanimalshelter.models.reports;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.DogImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(schema = "reports", name = "dog_reports")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class DogReport extends Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    @ToString.Exclude
    private PetOwner petOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    @ToString.Exclude
    private Dog dog;

    @OneToMany(targetEntity = DogImage.class, mappedBy = "dogReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<DogImage> images;


}
