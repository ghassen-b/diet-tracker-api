package com.example.diet_tracker_api.api;

import org.modelmapper.ModelMapper;

import com.example.diet_tracker_api.dto.MealIdDTO;
import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.service.MealService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/* Controller in charge of handling all requests coming to /meals.
 */
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractMealController {
    /**
     * Diet Service autowired object.
     */
    protected MealService mealService;
    /**
     * Automatic mapper.
     */
    private ModelMapper modelMapper;

    protected final MealOutDTO convertToDTO(Meal meal) {
        return modelMapper.map(meal, MealOutDTO.class);
    }

    protected final MealIdDTO convertToIdDTO(Meal meal) {
        return modelMapper.map(meal, MealIdDTO.class);
    }

    protected final Meal convertToEntity(MealInDTO mealInDTO) {
        return modelMapper.map(mealInDTO, Meal.class);
    }
}
