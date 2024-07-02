package com.example.diet_tracker_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.diet_tracker_api.model.Meal;

/**
 * DAO for for meals.
 */
public interface MealDAO extends JpaRepository<Meal, Long> {

}
