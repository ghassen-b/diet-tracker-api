package com.example.diet_tracker_api.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Meal representation in the DB.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotNull
    private String userId;

    @NotNull
    private LocalDate mealDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MealTime mealTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MealContent mealContent;

}
