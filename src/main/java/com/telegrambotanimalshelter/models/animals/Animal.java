package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.Shelter;
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

    @Column(name = "is_chipped")
    private boolean isChipped;

    @Column(name = "reported")
    private boolean reported;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public Animal(String nickname, boolean isChipped, LocalDateTime registeredAt, Shelter shelter){
        this.nickName = nickname;
        this.isChipped = isChipped;
        this.registeredAt = registeredAt;

    }
}
