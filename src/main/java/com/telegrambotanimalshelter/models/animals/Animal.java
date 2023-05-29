package com.telegrambotanimalshelter.models.animals;

import com.telegrambotanimalshelter.models.Shelter;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class Animal {

    @Column(name = "nickname")
    private String nickName;

    @Column(name = "is_chipped")
    private boolean isChipped;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "shelter")
    @Enumerated(EnumType.STRING)
    private Shelter shelter;

}
