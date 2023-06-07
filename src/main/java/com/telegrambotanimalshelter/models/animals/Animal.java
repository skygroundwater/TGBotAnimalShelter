package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.models.Shelter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


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

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public Animal(String nickname, boolean isChipped, LocalDateTime registeredAt, Shelter shelter){
        this.nickName = nickname;
        this.isChipped = isChipped;
        this.registeredAt = registeredAt;

    }
}
