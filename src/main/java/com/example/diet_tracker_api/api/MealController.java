package com.example.diet_tracker_api.api;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.diet_tracker_api.dto.MealIdDTO;
import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.model.Meal;
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
     * Automatic mapper
     */
    private final ModelMapper modelMapper;

    /**
     * Endpoint to get all saved meals for the current user.
     * 
     * @return List of MealOutDTO representations for all saved meals.
     */
    @GetMapping()
    public List<MealOutDTO> getUserMeals(@AuthenticationPrincipal Jwt jwt) {
        var userId = jwt.getSubject();
        return mealService.getUserMeals(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Endpoint to get details about a specific meal by id.
     * 
     * @param id Meal id in the DB
     * @return MealOutDTO representation of the matching Meal.
     */
    @GetMapping(value = "/{id}")
    public MealOutDTO getUserMealById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id) {
        var userId = jwt.getSubject();
        return convertToDTO(mealService.getUserMealById(userId, id));
    }

    /**
     * Endpoint to post a new meal instance.
     * 
     * @param mealInDTO Input to use to create the Meal instance.
     * @return MealOutDTO representation of the created Meal.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public MealIdDTO createMeal(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody MealInDTO mealInDTO) {
        var userId = jwt.getSubject();
        return convertToIdDTO(mealService.createMeal(userId, convertToEntity(mealInDTO)));
    }

    /**
     * Endpoint to delete a specific meal by id.
     * 
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id) {
        var userId = jwt.getSubject();
        mealService.deleteMealById(userId, id);
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
    public MealIdDTO editMealById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id,
            @Valid @RequestBody MealInDTO mealInDTO) {
        var userId = jwt.getSubject();
        return convertToIdDTO(mealService.editMealById(userId, id, convertToEntity(mealInDTO)));

    }

    protected MealOutDTO convertToDTO(Meal meal) {
        return modelMapper.map(meal, MealOutDTO.class);
    }

    protected MealIdDTO convertToIdDTO(Meal meal) {
        return modelMapper.map(meal, MealIdDTO.class);
    }

    protected Meal convertToEntity(MealInDTO mealInDTO) {
        return modelMapper.map(mealInDTO, Meal.class);
    }
}
