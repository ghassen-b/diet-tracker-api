package com.example.diet_tracker_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO used when we want to provide only the Meal id as an output.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealIdDTO {
    private Long id;
}
