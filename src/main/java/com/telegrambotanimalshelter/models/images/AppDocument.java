package com.telegrambotanimalshelter.models.images;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(schema = "images", name = "documents")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;

    @OneToOne
    private BinaryContent binaryContent;
    private Long fileSize;

    @OneToOne
    @JoinColumn(name = "cat_image_id")
    private CatImage catImage;

    @OneToOne
    @JoinColumn(name = "dog_image_id")
    private DogImage dogImage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppDocument appDocument = (AppDocument) o;
        return getId() != null && Objects.equals(getId(), appDocument.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
