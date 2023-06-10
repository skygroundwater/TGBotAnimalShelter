package com.telegrambotanimalshelter.services.petservice;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogsServiceImplTest {


    @InjectMocks
    private DogsServiceImpl dogsService;

    @Mock
    private DogsRepository dogsRepository;

    @Mock
    private Shelter shelter;

    @Mock
    private Dog dog;

    @Mock
    private PetOwner petOwner;

    private final List<Dog> dogs = new ArrayList<>();

    @Test
    void shouldFindPetsByPetOwner() {
        when(dogsService.findPetsByPetOwner(petOwner)).thenReturn(dogs);
        assertEquals(dogsRepository.findDogsByPetOwner(petOwner), dogs);
    }

    @Test
    void shouldGetAllPets() {
        when(dogsService.getAllPets()).thenReturn(dogs);
        assertEquals(dogsRepository.findAll(), dogs);
    }

    @Test
    void shouldSavePet() {
        when(dogsService.savePet(dog)).thenReturn(dog);
        assertEquals(dogsRepository.save(dog), dog);
    }

    @Test
    void shouldGetShelter() {
        assertEquals(dogsService.getShelter(), shelter);
    }
}