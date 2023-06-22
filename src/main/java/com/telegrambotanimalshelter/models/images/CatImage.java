package com.telegrambotanimalshelter.models.images;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.reports.CatReport;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(schema = "images", name = "cat_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatImage extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private CatReport report;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Cat cat;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private AppDocument appDocument;

}
