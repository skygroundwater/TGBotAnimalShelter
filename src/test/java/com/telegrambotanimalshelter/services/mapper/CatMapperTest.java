package com.telegrambotanimalshelter.services.mapper;

import com.telegrambotanimalshelter.config.ModelMapperConfig;
import com.telegrambotanimalshelter.dto.animals.CatDTO;
import com.telegrambotanimalshelter.exceptions.EmptyDTOException;
import com.telegrambotanimalshelter.models.animals.Cat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.telegrambotanimalshelter.services.mapper.CatMapper.convertToCat;
import static com.telegrambotanimalshelter.services.mapper.CatMapper.convertToCatDTO;
import static org.junit.jupiter.api.Assertions.*;

class CatMapperTest {

    ModelMapper mapper;

    @BeforeEach
    void modelMapper() {
        ModelMapperConfig mapperConfig = new ModelMapperConfig();
        mapper = mapperConfig.modelMapper();
    }
    @Test
    void shouldThrowExceptionWhenConvertNullToCat() {
        CatDTO catDTO = null;
        assertThrows(EmptyDTOException.class, () -> convertToCat(catDTO, mapper));
    }

    @Test
    void shouldConvertToCat() {
        CatDTO catDTO = new CatDTO("name", LocalDateTime.now(), "about");
        Cat cat = new Cat("name", false, LocalDateTime.now(), null, "about", null);

        assertEquals(convertToCat(catDTO, mapper).getNickName(), cat.getNickName());
        assertEquals(convertToCat(catDTO, mapper).getAbout(), cat.getAbout());
    }

    @Test
    void shouldConvertToCatDTOIfCatIsNull() {
        assertNull(convertToCatDTO(null, mapper));
    }

    @Test
    void shouldConvertToCatDTO() {
        Cat cat = new Cat("name", false, LocalDateTime.now(), null, "about", null);
        CatDTO catDTO = new CatDTO("name", LocalDateTime.now(), "about");

        assertEquals(Objects.requireNonNull(
                convertToCatDTO(cat, mapper)).getNickName(), catDTO.getNickName());
        assertEquals(Objects.requireNonNull(
                convertToCatDTO(cat, mapper)).getAbout(), catDTO.getAbout());
    }
}