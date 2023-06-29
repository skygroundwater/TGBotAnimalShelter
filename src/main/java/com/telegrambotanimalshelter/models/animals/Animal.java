package com.telegrambotanimalshelter.models.animals;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


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

    public Animal(String nickname, boolean sheltered, LocalDateTime registeredAt, String about) {
        this.nickName = nickname;
        this.sheltered = sheltered;
        this.registeredAt = registeredAt;
        this.about = about;
    }
}
