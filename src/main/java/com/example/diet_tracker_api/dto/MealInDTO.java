package com.example.diet_tracker_api.dto;

import java.time.LocalDate;

import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;

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
    private LocalDate mealDate;

    @NotNull
    private MealTime mealTime;

    @NotNull
    private MealContent mealContent;
}
