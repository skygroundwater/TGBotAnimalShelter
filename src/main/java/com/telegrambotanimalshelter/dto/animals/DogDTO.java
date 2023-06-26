package com.telegrambotanimalshelter.dto.animals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DogDTO {

    private String nickName;
    private boolean isChipped;
    private LocalDateTime registeredAt;
}