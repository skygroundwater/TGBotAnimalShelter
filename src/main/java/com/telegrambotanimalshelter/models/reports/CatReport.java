package com.telegrambotanimalshelter.models.reports;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.Image;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cat_reports")
@Getter
@Setter
@NoArgsConstructor
@Data
public class CatReport extends Report {

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
    private Cat cat;

    @OneToMany(targetEntity = CatImage.class, mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CatImage> images;


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
