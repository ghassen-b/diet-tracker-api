package com.example.diet_tracker_api.api;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.diet_tracker_api.dto.MealIdDTO;
import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.service.MealService;

@ExtendWith(SpringExtension.class)
public class MealControllerTest {

    @MockitoBean
    private MealService mockMealService;

    @MockitoBean
    private ModelMapper mockModelMapper;

    @TestConfiguration
    static class MealControllerTestContextConfiguration {
        @Bean
        public MealController addTestMealService(MealService mockMealService, ModelMapper mockModelMapper) {
            return new MealController(mockMealService, mockModelMapper) {
            };
        }
    }

    @Autowired
    private MealController mealController;

    Jwt mockJwt = Mockito.mock(Jwt.class);
    Meal mockMeal = Mockito.mock(Meal.class);
    MealIdDTO mockMealIdDTO = Mockito.mock(MealIdDTO.class);
    MealOutDTO mockMealOut = Mockito.mock(MealOutDTO.class);
    MealInDTO mockMealIn = Mockito.mock(MealInDTO.class);
    String mockUserId = "userId";
    Long mockMealId = 42L;

    @Test
    void whenGetUserMeals_givenValidJWT_thenMealsReturned() {
        Mockito.when(mockJwt.getSubject()).thenReturn(mockUserId);
        Mockito.when(mockMealService.getUserMeals(mockUserId)).thenReturn(List.of(mockMeal));
        Mockito.when(mockModelMapper.map(mockMeal, MealOutDTO.class)).thenReturn(mockMealOut);

        var output = mealController.getUserMeals(mockJwt);
        assertEquals(List.of(mockMealOut), output);
    }

    @Test
    void whenGetUserMealById_givenUserOwnsMeal_thenMealReturned() {
        Mockito.when(mockJwt.getSubject()).thenReturn(mockUserId);
        Mockito.when(mockMealService.getUserMealById(mockUserId, mockMealId)).thenReturn(mockMeal);
        Mockito.when(mockModelMapper.map(mockMeal, MealOutDTO.class)).thenReturn(mockMealOut);

        var output = mealController.getUserMealById(mockJwt, mockMealId);
        assertEquals(mockMealOut, output);
    }

    @Test
    void whenCreateMeal_givenUserOwnsMeal_thenMealIdReturned() {
        Mockito.when(mockJwt.getSubject()).thenReturn(mockUserId);
        Mockito.when(mockMealService.createMeal(mockUserId, mockMeal)).thenReturn(mockMeal);
        Mockito.when(mockModelMapper.map(mockMealIn, Meal.class)).thenReturn(mockMeal);
        Mockito.when(mockModelMapper.map(mockMeal, MealIdDTO.class)).thenReturn(mockMealIdDTO);

        var output = mealController.createMeal(mockJwt, mockMealIn);
        assertEquals(mockMealIdDTO, output);
    }

    @Test
    void whenDeleteMeal_givenUserOwnsMeal_thenMealDeleted() {
        Mockito.when(mockJwt.getSubject()).thenReturn(mockUserId);

        mealController.deleteMealById(mockJwt, mockMealId);
        Mockito.verify(mockMealService).deleteMealById(mockUserId, mockMealId);
    }

    @Test
    void whenEditMeal_givenUserOwnsMeal_thenMealReturned() {
        Mockito.when(mockJwt.getSubject()).thenReturn(mockUserId);
        Mockito.when(mockMealService.editMealById(mockUserId, mockMealId, mockMeal)).thenReturn(mockMeal);
        Mockito.when(mockModelMapper.map(mockMealIn, Meal.class)).thenReturn(mockMeal);
        Mockito.when(mockModelMapper.map(mockMeal, MealIdDTO.class)).thenReturn(mockMealIdDTO);

        var output = mealController.editMealById(mockJwt, mockMealId, mockMealIn);
        assertEquals(mockMealIdDTO, output);
    }
}
