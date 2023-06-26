package com.telegrambotanimalshelter.models.reports;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.images.CatImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(schema = "reports", name = "cat_reports")
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
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
    @ToString.Exclude
    private Cat cat;

    @OneToMany(mappedBy = "catReport", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<CatImage> images;


}
