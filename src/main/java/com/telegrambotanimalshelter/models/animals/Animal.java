package com.telegrambotanimalshelter.models.animals;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;


@Getter
@Setter
@ToString
@NoArgsConstructor
@MappedSuperclass
public abstract class Animal {

    @Column(name = "nickname")
    private String nickName;

    @Column(name = "sheltered")
    private boolean sheltered;

    @Column(name = "reported")
    private boolean reported;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "about")
    private String about;

    @Column (name = "photo")
    private byte[] photo;

    public Animal(String nickname, boolean sheltered, String about, byte[] photo) {
        this.nickName = nickname;
        this.sheltered = sheltered;
        this.registeredAt = LocalDateTime.now();
        this.about = about;
        this.photo = photo;
    }

    public Animal(String nickname, boolean sheltered, LocalDateTime registeredAt, String about, byte[] photo) {
        this.nickName = nickname;
        this.sheltered = sheltered;
        this.registeredAt = registeredAt;
        this.about = about;
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return sheltered == animal.sheltered && reported == animal.reported && Objects.equals(nickName, animal.nickName) && Objects.equals(registeredAt, animal.registeredAt) && Objects.equals(about, animal.about) && Arrays.equals(photo, animal.photo);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nickName, sheltered, reported, registeredAt, about);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }
}
