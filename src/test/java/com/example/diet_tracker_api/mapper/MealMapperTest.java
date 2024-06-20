package com.example.diet_tracker_api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.dto.UserOutDTO;
import com.example.diet_tracker_api.exception.MealInvalidInputException;
import com.example.diet_tracker_api.exception.UserNotFoundException;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;
import com.example.diet_tracker_api.model.User;
import com.example.diet_tracker_api.repository.UserDAO;

@ExtendWith(SpringExtension.class)
public class MealMapperTest {

    @TestConfiguration
    static class MealServiceTestContextConfiguration {
        @Bean
        public MealMapper addTestMealMapper(UserMapper mockUserMapper, UserDAO mockUserDAO) {
            return new MealMapper(mockUserMapper, mockUserDAO) {
            };
        }
    }

    @MockBean
    private UserMapper mockUserMapper;

    @MockBean
    private UserDAO mockUserDAO;

    @Autowired
    private MealMapper mealMapper;

    @Test
    void testEditFromInDTO_whenUserIdNotFound() {
        Long id = 42L;
        Mockito.when(mockUserDAO.findById(id)).thenReturn(Optional.empty());
        MealInDTO mockMealInDTO = Mockito.mock(MealInDTO.class);
        Meal meal = Mockito.mock(Meal.class);
        Mockito.when(mockMealInDTO.getUserId()).thenReturn(id);

        assertThrows(UserNotFoundException.class, () -> {
            mealMapper.editFromInDTO(mockMealInDTO, meal);
        },
        String.format("User with id=%d not found", id));

    }

    @SuppressWarnings("null")
    @Test
    void testEditFromInDTO_whenUserFieldNull() {
        Mockito.when(mockUserDAO.findById(null)).thenThrow(new InvalidDataAccessApiUsageException("a"));
        MealInDTO mockMealInDTO = Mockito.mock(MealInDTO.class);
        Meal meal = Mockito.mock(Meal.class);
        Mockito.when(mockMealInDTO.getUserId()).thenReturn(null);

        Exception exception = assertThrows(MealInvalidInputException.class, () -> {
            mealMapper.editFromInDTO(mockMealInDTO, meal);
        });

        assertTrue(exception.getMessage().contains("Meal creation/update failed because of invalid input."));
        assertTrue(exception.getMessage().contains("userId field cannot be null"));
    }

    @Test
    void testEditFromInDTO_whenValid() throws InvalidDataAccessApiUsageException {
        Long id = 42L;
        User newMealEater = Mockito.mock(User.class);
        User oldMealEater = Mockito.mock(User.class);
        Mockito.when(mockUserDAO.findById(id)).thenReturn(Optional.of(newMealEater));

        var mealInDTO = MealInDTO.builder()
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.of(1900, 1, 1))
                .mealTime(MealTime.BREAKFAST)
                .userId(id).build();
        var oldMeal = Meal.builder()
                .mealContent(MealContent.CHICKEN)
                .mealDate(LocalDate.now())
                .mealTime(MealTime.DINNER)
                .mealEater(oldMealEater)
                .build();

        var output = mealMapper.editFromInDTO(mealInDTO, oldMeal);

        assertEquals(output.getMealContent(), mealInDTO.getMealContent());
        assertEquals(output.getMealDate(), mealInDTO.getMealDate());
        assertEquals(output.getMealEater(), newMealEater);
        assertEquals(output.getMealTime(), mealInDTO.getMealTime());
        assertEquals(output.getId(), oldMeal.getId());
    }

    @Test
    void testFromEntity_givenMealEntity() {
        User mockUser = Mockito.mock(User.class);
        UserOutDTO mockUserOutDTO = Mockito.mock(UserOutDTO.class);
        Mockito.when(mockUserMapper.fromEntity(mockUser)).thenReturn(mockUserOutDTO);
        Meal meal = Meal.builder()
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.now())
                .mealTime(MealTime.BREAKFAST)
                .mealEater(mockUser)
                .build();
        var output = mealMapper.fromEntity(meal);
        assertEquals(output.getMealDate(), meal.getMealDate());
        assertEquals(output.getMealContent(), meal.getMealContent());
        assertEquals(output.getMealTime(), meal.getMealTime());
        assertEquals(output.getMealEater(), mockUserOutDTO);
    }

    @Test
    void testFromEntity_givenListMealEntities() {
        User mockUser = Mockito.mock(User.class);
        UserOutDTO mockUserOutDTO = Mockito.mock(UserOutDTO.class);
        Mockito.when(mockUserMapper.fromEntity(mockUser)).thenReturn(mockUserOutDTO);

        Meal meal1 = Meal.builder()
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.now())
                .mealTime(MealTime.BREAKFAST)
                .mealEater(mockUser)
                .build();
        Meal meal2 = Meal.builder()
                .mealContent(MealContent.CHICKEN)
                .mealDate(LocalDate.now())
                .mealTime(MealTime.DINNER)
                .mealEater(mockUser)
                .build();
        var outputList = mealMapper.fromEntity(List.of(meal1, meal2));
        var expectedOutputList = List.of(
                MealOutDTO.builder()
                        .mealContent(meal1.getMealContent())
                        .mealDate(meal1.getMealDate())
                        .mealTime(meal1.getMealTime())
                        .mealEater(mockUserOutDTO)
                        .build(),
                MealOutDTO.builder()
                        .mealContent(meal2.getMealContent())
                        .mealDate(meal2.getMealDate())
                        .mealTime(meal2.getMealTime())
                        .mealEater(mockUserOutDTO)
                        .build()

        );
        assertEquals(expectedOutputList, outputList);
    }

    @Test
    void testFromInDTO_whenUserIdNotFound() {
        Long id = 42L;
        Mockito.when(mockUserDAO.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            mealMapper.fromInDTO(MealInDTO.builder().userId(id).build());
        },
                String.format("User with id=%d not found", id));
    }

    @Test
    void testFromInDTO_whenValid() {
        Long id = 42L;
        User mealEater = Mockito.mock(User.class);
        var mealInDTO = MealInDTO.builder()
                .mealContent(MealContent.BEEF)
                .mealDate(LocalDate.now())
                .mealTime(MealTime.BREAKFAST)
                .userId(id).build();

        Mockito.when(mockUserDAO.findById(id)).thenReturn(Optional.of(mealEater));

        var output = mealMapper.fromInDTO(mealInDTO);
        var expectedOutput = Meal.builder()
                .mealEater(mealEater)
                .mealContent(mealInDTO.getMealContent())
                .mealDate(mealInDTO.getMealDate())
                .mealTime(mealInDTO.getMealTime())
                .build();

        assertEquals(output, expectedOutput);
    }
}
