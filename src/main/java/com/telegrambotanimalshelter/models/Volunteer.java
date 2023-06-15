package com.telegrambotanimalshelter.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name="volunteers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Volunteer {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="link")
    private String link;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Volunteer volunteer = (Volunteer) o;
        return getId() != null && Objects.equals(getId(), volunteer.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
