package com.example.diet_tracker_api.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.service.MealService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/* Controller in charge of handling all requests coming to /meals.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/meals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MealController {
    /**
     * Diet Service autowired object.
     */
    private final MealService mealService;

    /**
     * Endpoint to get all saved meals.
     * 
     * @return List of MealOutDTO representations for all saved meals.
     */
    @GetMapping()
    public List<MealOutDTO> getAllMeals() {
        return mealService.getAllMeals();
    }

    /**
     * Endpoint to get details about a specific meal by id.
     * 
     * @param id Meal id in the DB
     * @return MealOutDTO representation of the matching Meal.
     */
    @GetMapping(value = "/{id}")
    public MealOutDTO getMealById(@PathVariable("id") Long id) {
        return mealService.getMealById(id);

    }

    /**
     * Endpoint to post a new meal instance.
     * 
     * @param mealInDTO Input to use to create the Meal instance.
     * @return MealOutDTO representation of the created Meal.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public MealOutDTO createMeal(@Valid @RequestBody MealInDTO mealInDTO) {
        return mealService.createMeal(mealInDTO);

    }

    /**
     * Endpoint to delete a specific meal by id.
     * 
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealById(@PathVariable("id") Long id) {
        mealService.deleteMealById(id);

    }

    /**
     * Endpoint to edit a specific meal by id.
     * 
     * @param id        Meal id in the DB
     * @param mealInDTO MealInDTO object containing the wanted new information.
     * @return MealOutDTO representation of the edited instance.
     * 
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public MealOutDTO editMealById(@PathVariable("id") Long id, @Valid @RequestBody MealInDTO mealInDTO) {
        // TODO: return the instance ID, not the full DTO
        return mealService.editMealById(id, mealInDTO);

    }
}
