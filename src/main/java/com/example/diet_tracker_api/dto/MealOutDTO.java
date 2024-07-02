package com.example.diet_tracker_api.dto;

import java.time.LocalDate;

import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO used when describing a Meal instance as a response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealOutDTO {
    private UserOutDTO mealEater;

    private LocalDate mealDate;

    private MealTime mealTime;

    private MealContent mealContent;
    
}
