package com.telegrambotanimalshelter.services.mapper;

import com.telegrambotanimalshelter.dto.animals.CatDTO;
import com.telegrambotanimalshelter.exceptions.EmptyDTOException;
import com.telegrambotanimalshelter.models.animals.Cat;
import org.modelmapper.ModelMapper;

import java.util.Objects;

public class CatMapper {

    public static Cat convertToCat(CatDTO catDTO, ModelMapper modelMapper) {
        if (Objects.isNull(catDTO) || catDTO.getNickName() == null || catDTO.getRegisteredAt() == null) {
            throw new EmptyDTOException("Задан пустой запрос");
        } else {
            return modelMapper.map(catDTO, Cat.class);
        }
    }

    public static CatDTO convertToCatDTO(Cat cat, ModelMapper modelMapper) {
        return Objects.isNull(cat) ? null : modelMapper.map(cat, CatDTO.class);
    }
}
