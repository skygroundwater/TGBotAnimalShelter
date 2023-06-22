package com.telegrambotanimalshelter.models.images;

import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.DogReport;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "images", name = "dog_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogImage extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private DogReport report;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Dog dog;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private AppDocument appDocument;
}
