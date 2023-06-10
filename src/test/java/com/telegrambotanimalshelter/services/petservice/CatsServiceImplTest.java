package com.telegrambotanimalshelter.services.petservice;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatsServiceImplTest {


    @InjectMocks
    private CatsServiceImpl catsService;

    @Mock
    private CatsRepository catsRepository;

    @Mock
    private Shelter shelter;

    @Mock
    private Cat cat;

    @Mock
    private PetOwner petOwner;

    private final List<Cat> cats = new ArrayList<>();

    @Test
    void shouldFindPetsByPetOwner() {
        when(catsService.findPetsByPetOwner(petOwner)).thenReturn(cats);
        assertEquals(catsRepository.findCatsByPetOwner(petOwner), cats);
    }

    @Test
    void shouldGetAllPets() {
        when(catsService.getAllPets()).thenReturn(cats);
        assertEquals(catsRepository.findAll(), cats);
    }

    @Test
    void shouldSavePet() {
        when(catsService.savePet(cat)).thenReturn(cat);
        assertEquals(catsRepository.save(cat), cat);
    }

    @Test
    void shouldGetShelter() {
        assertEquals(catsService.getShelter(), shelter);
    }
}