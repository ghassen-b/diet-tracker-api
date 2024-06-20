package com.example.diet_tracker_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.exception.MealInvalidInputException;
import com.example.diet_tracker_api.exception.MealNotFoundException;
import com.example.diet_tracker_api.mapper.MealMapper;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.repository.MealDAO;

import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolationException;

@ExtendWith(SpringExtension.class)
public class MealServiceTest {

    @TestConfiguration
    static class MealServiceTestContextConfiguration {
        @Bean
        public MealService addTestMealService(MealDAO mockMealDAO, MealMapper mockMealMapper) {
            return new MealService(mockMealDAO, mockMealMapper) {
            };
        }
    }

    @MockBean
    private MealDAO mockMealDAO;

    @MockBean
    private MealMapper mockMealMapper;

    @Autowired
    private MealService mealService;

    private final Long id = 42L;

    @Test
    void givenValidInput_whenCreateMeal_thenMealCreated() {
        var mockMealInDTO = Mockito.mock(MealInDTO.class);
        var mockMeal = Mockito.mock(Meal.class);
        var mockCreatedMeal = Mockito.mock(Meal.class);
        var mockMealOutDTO = Mockito.mock(MealOutDTO.class);

        Mockito.when(mockMealMapper.fromInDTO(mockMealInDTO)).thenReturn(mockMeal);
        Mockito.when(mockMealDAO.save(mockMeal)).thenReturn(mockCreatedMeal);
        Mockito.when(mockMealMapper.fromEntity(mockCreatedMeal)).thenReturn(mockMealOutDTO);

        assertEquals(mockMealOutDTO, mealService.createMeal(mockMealInDTO));
    }

    @Test
    void givenConstraintViolationExceptionThrown_whenCreateMeal_thenCorrectExceptionThrown() {
        var mockMealInDTO = Mockito.mock(MealInDTO.class);
        var mockMeal = Mockito.mock(Meal.class);

        Mockito.when(mockMealMapper.fromInDTO(mockMealInDTO)).thenReturn(mockMeal);
        Mockito.when(mockMealDAO.save(mockMeal)).thenThrow(new ConstraintViolationException(Collections.emptySet()));

        Exception exception = assertThrows(MealInvalidInputException.class, () -> {
            mealService.createMeal(mockMealInDTO);
        });

        assertTrue(exception.getMessage().contains("Meal creation/update failed because of invalid input."));
    }

    @Test
    void givenMealExists_whenDeleteMealById_thenMealDeleted() {
        var mockMeal = Mockito.mock(Meal.class);

        Mockito.when(mockMealDAO.findById(id)).thenReturn(Optional.of(mockMeal));

        mealService.deleteMealById(id);

        Mockito.verify(mockMealDAO).delete(mockMeal);

    }

    @Test
    void givenMealDoesNotExist_whenDeleteMealById_thenExceptionRaised() {
        Mockito.when(mockMealDAO.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                MealNotFoundException.class,
                () -> {
                    mealService.deleteMealById(id);
                },
                String.format("Meal with id=%d not found", id));
    }

    @Test
    void givenEverythingOK_whenEditMealById_thenMealEdited() {
        var mockMeal = Mockito.mock(Meal.class);
        var mockMealInDTO = Mockito.mock(MealInDTO.class);
        var mockUpdatedMeal = Mockito.mock(Meal.class);
        var mockMealOutDTO = Mockito.mock(MealOutDTO.class);

        Mockito.when(mockMealDAO.findById(id)).thenReturn(Optional.of(mockMeal));
        Mockito.when(mockMealMapper.editFromInDTO(mockMealInDTO, mockMeal)).thenReturn(mockUpdatedMeal);
        Mockito.when(mockMealDAO.save(mockUpdatedMeal)).thenReturn(mockUpdatedMeal);
        Mockito.when(mockMealMapper.fromEntity(mockUpdatedMeal)).thenReturn(mockMealOutDTO);

        assertEquals(mockMealOutDTO, mealService.editMealById(id, mockMealInDTO));

    }

    @Test
    void givenTransactionSystemExceptionExceptionThrown_whenEditMealById_thenCorrectExceptionThrown() {
        var mockMeal = Mockito.mock(Meal.class);
        var mockMealInDTO = Mockito.mock(MealInDTO.class);
        var mockUpdatedMeal = Mockito.mock(Meal.class);

        Mockito.when(mockMealDAO.findById(id)).thenReturn(Optional.of(mockMeal));
        Mockito.when(mockMealMapper.editFromInDTO(mockMealInDTO, mockMeal)).thenReturn(mockUpdatedMeal);
        var thrownException = new TransactionSystemException("a",
                new RollbackException(
                        new RuntimeException("toto")));
        Mockito.when(mockMealDAO.save(mockUpdatedMeal)).thenThrow(
                thrownException);

        Exception exception = assertThrows(TransactionSystemException.class, () -> {
            mealService.editMealById(id, mockMealInDTO);
        });
        assertEquals(thrownException, exception);

    }

    @Test
    void givenMealDoesNotExist_whenEditMealById_thenCorrectExceptionThrown() {
        Mockito.when(mockMealDAO.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                MealNotFoundException.class,
                () -> {
                    mealService.editMealById(id, Mockito.mock(MealInDTO.class));
                },
                String.format("Meal with id=%d not found", id));

    }

    @Test
    void givenMealsExist_whenGetAllMeals_thenMealsReturned() {
        var mockMeal = Mockito.mock(Meal.class);
        var mockMealOutDTO = Mockito.mock(MealOutDTO.class);
        var listMockMeals = List.of(mockMeal);
        var listMockMealDTOs = List.of(mockMealOutDTO);

        Mockito.when(mockMealDAO.findAll()).thenReturn(listMockMeals);
        Mockito.when(mockMealMapper.fromEntity(listMockMeals)).thenReturn(listMockMealDTOs);

        assertEquals(listMockMealDTOs, mealService.getAllMeals());
    }

    @Test
    void givenNoMealsExist_whenGetAllMeals_thenNoMealsReturned() {
        Mockito.when(mockMealDAO.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(mockMealMapper.fromEntity(Collections.emptyList())).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), mealService.getAllMeals());
    }

    @Test
    void givenMealExists_whenGetMealById_thenMealReturned() {
        var mockMeal = Mockito.mock(Meal.class);
        var mockMealOutDTO = Mockito.mock(MealOutDTO.class);

        Mockito.when(mockMealDAO.findById(id)).thenReturn(Optional.of(mockMeal));
        Mockito.when(mockMealMapper.fromEntity(mockMeal)).thenReturn(mockMealOutDTO);

        assertEquals(mockMealOutDTO, mealService.getMealById(id));

    }

    @Test
    void givenMealDoesNotExist_whenGetMealById_thenExceptionRaised() {
        Mockito.when(mockMealDAO.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                MealNotFoundException.class, () -> {
                    mealService.getMealById(id);
                },
                String.format("Meal with id=%d not found", id));

    }
}
