package com.telegrambotanimalshelter.services.mapper;

import com.telegrambotanimalshelter.dto.animals.DogDTO;
import com.telegrambotanimalshelter.exceptions.EmptyDTOException;
import com.telegrambotanimalshelter.models.animals.Dog;
import org.modelmapper.ModelMapper;

import java.util.Objects;

public class DogMapper {

    public static Dog convertToDog(DogDTO dogDTO, ModelMapper modelMapper) {
        if (Objects.isNull(dogDTO) || dogDTO.getNickName() == null || dogDTO.getRegisteredAt() == null) {
            throw new EmptyDTOException("Задан пустой запрос");
        } else {
            return modelMapper.map(dogDTO, Dog.class);
        }
    }

    public static DogDTO convertToDogDTO(Dog dog, ModelMapper modelMapper) {
        return Objects.isNull(dog) ? null : modelMapper.map(dog, DogDTO.class);
    }
}
