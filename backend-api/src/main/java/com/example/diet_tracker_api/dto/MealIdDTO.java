package com.example.diet_tracker_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used when we want to provide only the Meal id as an output.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealIdDTO {
    /**
     * Meal id.
     */
    @Schema(description = "Meal Id", example = "42")
    private Long id;
}
