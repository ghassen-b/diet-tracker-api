package com.example.diet_tracker_api.dto;

import java.time.LocalDate;

import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used when receiving the description of a Meal instance to be created.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealInDTO {
    @NotNull
    private Long userId;

    @NotNull
    private LocalDate mealDate;

    @NotNull
    private MealTime mealTime;

    @NotNull
    private MealContent mealContent;
}
