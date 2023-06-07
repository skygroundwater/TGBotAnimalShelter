package com.telegrambotanimalshelter.services.petownerservice;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.repositories.PetOwnersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetOwnersServiceImplTest {

    private final PetOwner petOwner = new PetOwner(1L, "null", "null", "null", LocalDateTime.now(), true);

    @InjectMocks
    private PetOwnersServiceImpl petOwnersServiceImpl;

    @Mock
    private PetOwnersRepository petOwnersRepository;

    @Mock
    private PetOwnersService petOwnersService;

    @Test
    void shouldSavePetOwnerToDBTest() {
        assertThrows(NotValidDataException.class, () -> petOwnersServiceImpl.savePetOwnerToDB(null));

        when(petOwnersServiceImpl.savePetOwnerToDB(petOwner)).thenReturn(petOwner);

        assertEquals(petOwnersServiceImpl.savePetOwnerToDB(petOwner), petOwner);
    }

    @Test
    void shouldFindPetOwnerByIdTest() {
        assertThrows(NotFoundInDataBaseException.class, () -> petOwnersServiceImpl.findPetOwnerById(null));

        when(petOwnersService.findPetOwnerById(1L)).thenReturn(petOwner);

        assertEquals(petOwnersService.findPetOwnerById(1L), petOwner);

    }

    @Test
    void shouldFindActualPetOwnersTest() {
        List<PetOwner> list = List.of(petOwner);

        when(petOwnersRepository.findPetOwnersByHasPetsTrue()).thenReturn(list);

        assertEquals(petOwnersRepository.findPetOwnersByHasPetsTrue(), list);
    }

    @Test
    void shouldGetAllPetOwnersTest() {
        List<PetOwner> list = List.of(petOwner);

        when(petOwnersService.getAllPetOwners()).thenReturn(list);

        assertEquals(petOwnersService.getAllPetOwners(), list);
    }
}