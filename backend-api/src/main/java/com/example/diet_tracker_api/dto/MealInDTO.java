package com.example.diet_tracker_api.dto;

import java.time.LocalDate;

import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used when receiving the description of a Meal instance to be created.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealInDTO {
    @NotNull
    @Schema(description = "Meal Date in Local Timezone", example = "2020-11-29")
    private LocalDate mealDate;

    @NotNull
    @Schema(description = "Meal Time", example = "LUNCH")
    private MealTime mealTime;

    @NotNull
    @Schema(description = "Meal Content", example = "VEGETARIAN")
    private MealContent mealContent;
}
