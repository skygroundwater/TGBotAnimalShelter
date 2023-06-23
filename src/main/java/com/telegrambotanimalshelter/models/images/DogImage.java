package com.telegrambotanimalshelter.models.images;

import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.DogReport;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "images", name = "dog_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogImage extends AppImage{

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dog_id")
    private Dog dog;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private DogReport dogReport;

}
