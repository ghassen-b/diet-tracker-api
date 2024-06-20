package com.example.diet_tracker_api.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.diet_tracker_api.model.Meal;

/**
 * DAO for for meals.
 */
public interface MealDAO extends CrudRepository<Meal, Long> {

}
