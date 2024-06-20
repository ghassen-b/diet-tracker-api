package com.example.diet_tracker_api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.exception.MealInvalidInputException;
import com.example.diet_tracker_api.exception.UserNotFoundException;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.model.User;
import com.example.diet_tracker_api.repository.UserDAO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MealMapper {

    private final UserMapper userMapper;

    private final UserDAO userDAO;

    /**
     * Converts a Meal instance into a MealOutDTO instance.
     * 
     * @param meal Meal instance to be converted.
     * @return MealOutDTO representation of the input instance.
     */
    public MealOutDTO fromEntity(Meal meal) {
        return MealOutDTO.builder()
                .mealDate(meal.getMealDate())
                .mealTime(meal.getMealTime())
                .mealContent(meal.getMealContent())
                .mealEater(userMapper.fromEntity(meal.getMealEater()))
                .build();
    }

    /**
     * Converts a List of Meal instances into a List of MealOutDTO instances.
     * 
     * @param listMeals List of Meal instances to convert.
     * @return List of corresponding MealOutDTO instances.
     */
    public List<MealOutDTO> fromEntity(List<Meal> listMeals) {
        return listMeals.stream().map(this::fromEntity).toList();
    }

    /**
     * Converts a MealInDTO instance into a Meal instance.
     * 
     * @param mealInDTO MealInDTO instance to be used to create a Meal instance
     * @return the created Meal instance.
     */
    public Meal fromInDTO(MealInDTO mealInDTO) {
        User mealEater = getUserFromUserId(mealInDTO);
        return Meal.builder()
                .mealEater(mealEater)
                .mealContent(mealInDTO.getMealContent())
                .mealDate(mealInDTO.getMealDate())
                .mealTime(mealInDTO.getMealTime())
                .build();
    }

    /*
     
     */
    /**
     * Updates a provided Meal instance with the content of an input MealInDTO
     * instance.
     * 
     * @param mealInDTO MealInDTO containing the new information to be saved.
     * @param meal      Meal instance to be edited.
     * @return edited Meal instance.
     */
    public Meal editFromInDTO(MealInDTO mealInDTO, Meal meal) {
        meal.setMealContent(mealInDTO.getMealContent());
        meal.setMealDate(mealInDTO.getMealDate());
        meal.setMealEater(getUserFromUserId(mealInDTO));
        meal.setMealTime(mealInDTO.getMealTime());
        return meal;
    }

    /**
     * Safe method to get a User instance from the DAO based on the MealInDTO's
     * userId field.
     * 
     * @param mealInDTO
     * @return Matching User instance
     * @throws MealInvalidInputException is the MealInDTO's userId field is null.
     */
    private User getUserFromUserId(MealInDTO mealInDTO) {
        if (mealInDTO.getUserId() == null) {
            throw new MealInvalidInputException(mealInDTO, "userId field cannot be null");
        }
        return userDAO.findById(
                mealInDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(mealInDTO.getUserId()));
    }

}
