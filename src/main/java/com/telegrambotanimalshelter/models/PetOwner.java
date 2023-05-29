package com.telegrambotanimalshelter.models;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="petowners")
@Getter
@Setter
@ToString
public class PetOwner {

    @Id
    @Column(name="petowner_id")
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "has_pets")
    private boolean hasPets;

    @OneToMany(mappedBy = "petOwner")
    @ToString.Exclude
    private List<Cat> cats;

    @OneToMany(mappedBy = "petOwner")
    @ToString.Exclude
    private List<Dog> dogs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PetOwner petOwner = (PetOwner) o;
        return getId() != null && Objects.equals(getId(), petOwner.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
