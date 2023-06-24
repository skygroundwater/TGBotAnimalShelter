package com.telegrambotanimalshelter.models.images;


import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.reports.CatReport;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "images", name = "cat_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatImage extends AppImage{

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cat_id")
    private Cat cat;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private CatReport catReport;



}
