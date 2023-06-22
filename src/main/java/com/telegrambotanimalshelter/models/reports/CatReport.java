package com.telegrambotanimalshelter.models.reports;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.images.CatImage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;

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

    @OneToMany(targetEntity = CatImage.class, mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CatImage> images;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CatReport catReport = (CatReport) o;
        return getId() != null && Objects.equals(getId(), catReport.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
