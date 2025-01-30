package com.example.diet_tracker_api.api;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.example.diet_tracker_api.service.MealService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/* Controller in charge of handling all requests coming to /meals.
 */

@RestController
@RequestMapping(value = "/meals", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyAuthority('DIET_APP_USER')")
public class MealUserController extends AbstractMealController {
    /**
     * Constructor for the controller.
     *
     * @param mealService Meal service
     * @param modelMapper Meal Entity <-> DTOs mapper.
     */
    public MealUserController(MealService mealService, ModelMapper modelMapper) {
        super(mealService, modelMapper);
    }

    /**
     * Endpoint to get all saved meals for the current user.
     *
     * @param jwt JWT token providing authentication
     * @return List of MealOutDTO representations for all saved meals.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of Meal details returned", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MealOutDTO.class))) }) })
    @Operation(summary = "Get a list of all meals")
    public List<MealOutDTO> getUserMeals(@AuthenticationPrincipal Jwt jwt) {
        var userId = jwt.getSubject();
        return mealService.getUserMeals(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Endpoint to get details about a specific meal by id.
     *
     * @param jwt Request JWT token (for authentication)
     * @param id  Meal id in the DB
     * @return MealOutDTO representation of the matching Meal.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meal details returned", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MealOutDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id format", content = @Content),
            @ApiResponse(responseCode = "404", description = "Meal not found", content = @Content) })
    @Operation(summary = "Get the details of a meal")
    public MealOutDTO getUserMealById(@AuthenticationPrincipal Jwt jwt,
            @PathVariable() @Parameter(name = "id", description = "Meal id", example = "1") Long id) {
        var userId = jwt.getSubject();
        return convertToDTO(mealService.getUserMealById(userId, id));
    }

    /**
     * Endpoint to post a new meal instance.
     *
     * @param jwt       Request JWT token (for authentication)
     * @param mealInDTO Input to use to create the Meal instance.
     * @return MealOutDTO representation of the created Meal.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meal created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MealIdDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content) })
    @Operation(summary = "Create a meal from the provided input")
    @ResponseStatus(code = HttpStatus.CREATED)
    public MealIdDTO createMeal(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody MealInDTO mealInDTO) {
        var userId = jwt.getSubject();
        return convertToIdDTO(mealService.createMeal(userId, convertToEntity(mealInDTO)));
    }

    /**
     * Endpoint to delete a specific meal by id.
     *
     * @param jwt Request JWT token (for authentication)
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meal deleted"),
            @ApiResponse(responseCode = "404", description = "Meal not found", content = @Content) })
    @Operation(summary = "Delete a meal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealById(@AuthenticationPrincipal Jwt jwt,
            @PathVariable() @Parameter(name = "id", description = "Meal id", example = "1") Long id) {
        var userId = jwt.getSubject();
        mealService.deleteMealById(userId, id);
    }

    /**
     * Endpoint to edit a specific meal by id.
     *
     * @param jwt       Request JWT token (for authentication)
     * @param mealId    Meal id in the DB
     * @param mealInDTO MealInDTO object containing the wanted new information.
     * @return MealOutDTO representation of the edited instance.
     *
     */
    @PutMapping(value = "/{mealId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meal edited", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MealIdDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content) })
    @Operation(summary = "Edit the details of a meal")
    @ResponseStatus(HttpStatus.CREATED)
    public MealIdDTO editMealById(@AuthenticationPrincipal Jwt jwt,
            @PathVariable() @Parameter(name = "mealId", description = "Meal id", example = "1") Long mealId,
            @Valid @RequestBody MealInDTO mealInDTO) {
        var userId = jwt.getSubject();
        return convertToIdDTO(mealService.editMealById(userId, mealId, convertToEntity(mealInDTO)));

    }
}
