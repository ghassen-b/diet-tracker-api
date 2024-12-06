package com.example.diet_tracker_api.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.diet_tracker_api.ModelMapperConfiguration;
import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;
import com.example.diet_tracker_api.service.MealService;

@ExtendWith(SpringExtension.class)
public class MealMapperTest {

    @MockitoBean
    private MealService mockMealService;

    @Autowired
    private MealController mealController;

    @TestConfiguration
    static class MealMapperTestContextConfiguration {
        @Bean
        public MealController addTestMealController(MealService mockMealService) {
            return new MealController(mockMealService, new ModelMapperConfiguration().modelMapper()) {
            };
        }
    }

    @Test
    void testMealFromMealInDTO() {
        var mealInDTO = MealInDTO.builder()
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.of(1900, 1, 1))
                .mealTime(MealTime.BREAKFAST)
                .build();

        var output = mealController.convertToEntity(mealInDTO);

        assertEquals(mealInDTO.getMealContent(), output.getMealContent());
        assertEquals(mealInDTO.getMealDate(), output.getMealDate());
        assertEquals(mealInDTO.getMealTime(), output.getMealTime());
        assertEquals(null, output.getId());
        assertEquals(null, output.getUserId());
    }

    @Test
    void testMealOutDTOFromEntity() {
        var meal = Meal.builder()
                .Id(42L)
                .userId("someUser")
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.of(1900, 1, 1))
                .mealTime(MealTime.BREAKFAST)
                .build();

        var output = mealController.convertToDTO(meal);
        assertEquals(meal.getId(), output.getId());
        assertEquals(meal.getUserId(), output.getUserId());
        assertEquals(meal.getMealContent(), output.getMealContent());
        assertEquals(meal.getMealDate(), output.getMealDate());
        assertEquals(meal.getMealTime(), output.getMealTime());

    }

}
