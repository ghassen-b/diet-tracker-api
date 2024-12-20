package com.example.diet_tracker_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import com.example.diet_tracker_api.exception.MealNotFoundException;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.repository.MealDAO;

@ExtendWith(SpringExtension.class)
public class MealServiceTest {

    @TestConfiguration
    static class MealServiceTestContextConfiguration {
        @Bean
        public MealService addTestMealService(MealDAO mockMealDAO) {
            return new MealService(mockMealDAO) {
            };
        }
    }

    @MockitoBean
    private MealDAO mockMealDAO;

    @Autowired
    private MealService mealService;

    private final Long id = 42L;

    private final String userId = "someUser";

    @Test
    void testGetAllMeals() {
        var mockMeal = Mockito.mock(Meal.class);

        Mockito.when(mockMealDAO.findByUserId(userId)).thenReturn(List.of(mockMeal));

        assertEquals(List.of(mockMeal), mealService.getUserMeals(userId));
    }

    @Test
    void givenMealExists_whenGetMealById_thenReturned() {
        var mockMeal = Mockito.mock(Meal.class);

        Mockito.when(mockMealDAO.findByIdAndUserId(id, userId)).thenReturn(Optional.of(mockMeal));

        assertEquals(mockMeal, mealService.getUserMealById(userId, id));
    }

    @Test
    void givenMealDoesNotExist_whenGetMealById_thenExceptionRaised() {
        Mockito.when(mockMealDAO.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThrows(
                MealNotFoundException.class,
                () -> {
                    mealService.getUserMealById(userId, id);
                },
                String.format("Meal with id=%d not found", id));
    }

    @Test
    void givenValidInput_whenCreateMeal_thenMealCreated() {
        var mockInputMeal = Mockito.mock(Meal.class);
        var mockCreatedMeal = Mockito.mock(Meal.class);

        Mockito.when(mockMealDAO.save(mockInputMeal)).thenReturn(mockCreatedMeal);

        assertEquals(mockCreatedMeal, mealService.createMeal(userId, mockInputMeal));
    }

    @Test
    void givenDAOSaveFails_whenCreateMeal_thenExceptionIsNotCatched() {
        var mockMeal = Mockito.mock(Meal.class);
        Mockito.when(mockMealDAO.save(mockMeal)).thenThrow(new TransactionSystemException("toto"));

        assertThrows(
                TransactionSystemException.class,
                () -> {
                    mealService.createMeal(userId, mockMeal);
                },
                String.format("toto"));

    }

    @Test
    void givenMealExists_whenDeleteMealById_thenMealDeleted() {
        var mockMeal = Mockito.mock(Meal.class);

        Mockito.when(mockMealDAO.findByIdAndUserId(id, userId)).thenReturn(Optional.of(mockMeal));

        mealService.deleteMealById(userId, id);

        Mockito.verify(mockMealDAO).delete(mockMeal);
    }

    @Test
    void givenMealDoesNotExist_whenDeleteMealById_thenExceptionRaised() {
        Mockito.when(mockMealDAO.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThrows(
                MealNotFoundException.class,
                () -> {
                    mealService.deleteMealById(userId, id);
                },
                String.format("Meal with id=%d not found", id));
    }

    @Test
    void givenDAODeleteFails_whenDeleteMealById_thenExceptionIsNotCatched() {
        var mockMeal = Mockito.mock(Meal.class);
        Mockito.when(mockMealDAO.findByIdAndUserId(id, userId)).thenReturn(Optional.of(mockMeal));
        Mockito.doThrow(new TransactionSystemException("toto")).when(mockMealDAO).delete(mockMeal);

        assertThrows(
                TransactionSystemException.class,
                () -> {
                    mealService.deleteMealById(userId, id);
                },
                String.format("toto"));
    }

    @Test
    void givenEverythingOK_whenEditMealById_thenMealEdited() {
        var mockMealInput = Mockito.mock(Meal.class);
        var mockMealSaved = Mockito.mock(Meal.class);

        Mockito.when(mockMealDAO.existsByIdAndUserId(id, userId)).thenReturn(true);
        Mockito.when(mockMealDAO.save(mockMealInput)).thenReturn(mockMealSaved);

        assertEquals(mockMealSaved, mealService.editMealById(userId, id, mockMealInput));

        Mockito.verify(mockMealInput).setId(id);

    }

    @Test
    void givenMealDoesNotExist_whenEditMealById_thenCorrectExceptionThrown() {
        Mockito.when(mockMealDAO.existsByIdAndUserId(id, userId)).thenReturn(false);

        assertThrows(
                MealNotFoundException.class,
                () -> {
                    mealService.editMealById(userId, id, Mockito.mock(Meal.class));
                },
                String.format("Meal with id=%d not found", id));

    }

    @Test
    void givenDAOSaveFails_whenEditMealById_thenExceptionIsNotCatched() {
        Mockito.when(mockMealDAO.existsByIdAndUserId(id, userId)).thenReturn(true);
        var mockMeal = Mockito.mock(Meal.class);
        Mockito.when(mockMealDAO.save(mockMeal)).thenThrow(new TransactionSystemException("toto"));

        assertThrows(
                TransactionSystemException.class,
                () -> {
                    mealService.editMealById(userId, id, mockMeal);
                },
                String.format("toto"));

    }
}
