package com.telegrambotanimalshelter.services.mapper;

import com.telegrambotanimalshelter.config.ModelMapperConfig;
import com.telegrambotanimalshelter.dto.animals.DogDTO;
import com.telegrambotanimalshelter.exceptions.EmptyDTOException;
import com.telegrambotanimalshelter.models.animals.Dog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.telegrambotanimalshelter.services.mapper.DogMapper.convertToDog;
import static com.telegrambotanimalshelter.services.mapper.DogMapper.convertToDogDTO;
import static org.junit.jupiter.api.Assertions.*;

class DogMapperTest {

    ModelMapper mapper;

    @BeforeEach
    void modelMapper() {
        ModelMapperConfig mapperConfig = new ModelMapperConfig();
        mapper = mapperConfig.modelMapper();
    }

    @Test
    void shouldThrowExceptionWhenConvertNullToDog() {
        DogDTO dogDTO = null;
        assertThrows(EmptyDTOException.class, () -> convertToDog(dogDTO, mapper));
    }

    @Test
    void shouldConvertToCat() {
        DogDTO dogDTO = new DogDTO("name", LocalDateTime.now(), "about");
        Dog dog = new Dog("name", false, LocalDateTime.now(), null, "about", null);

        assertEquals(convertToDog(dogDTO, mapper).getNickName(), dog.getNickName());
        assertEquals(convertToDog(dogDTO, mapper).getAbout(), dog.getAbout());
    }

    @Test
    void shouldConvertToCatDTOIfCatIsNull() {
        assertNull(convertToDogDTO(null, mapper));
    }

    @Test
    void shouldConvertToCatDTO() {
        DogDTO dogDTO = new DogDTO("name", LocalDateTime.now(), "about");
        Dog dog = new Dog("name", false, LocalDateTime.now(), null, "about", null);

        assertEquals(Objects.requireNonNull(
                convertToDogDTO(dog, mapper)).getNickName(), dogDTO.getNickName());
        assertEquals(Objects.requireNonNull(
                convertToDogDTO(dog, mapper)).getAbout(), dogDTO.getAbout());
    }
}