package com.example.diet_tracker_api.dto;

import java.time.LocalDate;

import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used when describing a Meal instance as a response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealOutDTO {
    /**
     * Meal id.
     */
    @Schema(description = "Meal Id", example = "42")
    private Long id;

    /**
     * Meal-owner id.
     */
    @Schema(description = "User Id who posted the Meal", example = "550e8400-e29b-41d4-a716-446655440000")
    private String userId;

    /**
     * Meal date.
     */
    @Schema(description = "Meal Date in Local Timezone", example = "2020-11-29")
    private LocalDate mealDate;

    /**
     * Meal time (lunch, etc.).
     */
    @Schema(description = "Meal Time", example = "LUNCH")
    private MealTime mealTime;

    /**
     * Meal content (beef, etc.).
     */
    @Schema(description = "Meal Content", example = "VEGETARIAN")
    private MealContent mealContent;

}
