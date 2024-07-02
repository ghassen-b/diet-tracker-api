package com.example.diet_tracker_api.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.diet_tracker_api.ModelMapperConfiguration;
import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.dto.UserOutDTO;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;
import com.example.diet_tracker_api.model.User;
import com.example.diet_tracker_api.repository.UserDAO;
import com.example.diet_tracker_api.service.MealService;

@ExtendWith(SpringExtension.class)
public class MealMapperTest {

    @MockBean
    private UserDAO mockUserDAO;

    @MockBean
    private MealService mockMealService;

    @Autowired
    private MealController mealController;

    @TestConfiguration
    static class MealMapperTestContextConfiguration {
        @Bean
        public MealController addTestMealController(MealService mockMealService, UserDAO mockUserDAO) {
            return new MealController(mockMealService, new ModelMapperConfiguration(mockUserDAO).modelMapper()) {
            };
        }
    }

    @Test
    void testMealFromMealInDTO() {
        var mockUser = Mockito.mock(User.class);

        Mockito.when(mockUserDAO.findById(42L)).thenReturn(Optional.of(mockUser));

        var mealInDTO = MealInDTO.builder()
                .mealEaterId(42L)
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.of(1900, 1, 1))
                .mealTime(MealTime.BREAKFAST)
                .build();

        var output = mealController.convertToEntity(mealInDTO);

        // The following also checks that the id is null !
        assertEquals(output, Meal.builder()
                .mealContent(mealInDTO.getMealContent())
                .mealTime(mealInDTO.getMealTime())
                .mealDate(mealInDTO.getMealDate())
                .mealEater(mockUser)
                .build());

    }

    @Test
    void givenUserDoesNotExist_whenMapMealFromMealInDTO_thenExceptionRaised() {
        Mockito.when(mockUserDAO.findById(42L)).thenReturn(Optional.empty());

        assertThrows(
                MappingException.class,
                () -> {
                    mealController.convertToEntity(Mockito.mock(MealInDTO.class));
                },
                String.format("User with id=%d not found", 42L));
    }

    @Test
    void testMealOutDTOFromEntity() {
        var user = User.builder()
                .firstName("toto")
                .lastName("tata")
                .email("tutu").build();

        var meal = Meal.builder()
                .Id(42L)
                .mealEater(user)
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.of(1900, 1, 1))
                .mealTime(MealTime.BREAKFAST)
                .build();

        var output = mealController.convertToDTO(meal);

        // The following also checks that the id is null !
        assertEquals(output, MealOutDTO.builder()
                .mealContent(meal.getMealContent())
                .mealTime(meal.getMealTime())
                .mealDate(meal.getMealDate())
                .mealEater(UserOutDTO.builder()
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build())
                .build());

    }

}
