package com.telegrambotanimalshelter.models.reports;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.DogImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "dog_reports")
@NoArgsConstructor
@Getter
@Setter
@Data
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
    private Dog dog;

    @OneToMany(targetEntity = DogImage.class, mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DogImage> images;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
