package com.telegrambotanimalshelter.dto.animals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatDTO {

    private String nickName;
    private LocalDateTime registeredAt;
    private String about;
}
