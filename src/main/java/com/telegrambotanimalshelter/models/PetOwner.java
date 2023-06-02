package com.telegrambotanimalshelter.models;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="petowners")
@NoArgsConstructor
@AllArgsConstructor
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

    public PetOwner(Long id, String firstName,
                    String lastName, String userName,
                    LocalDateTime registeredAt, boolean hasPets){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registeredAt = registeredAt;
        this.userName = userName;
        this.hasPets = hasPets;
    }

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
