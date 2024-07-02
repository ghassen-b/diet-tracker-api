package com.example.diet_tracker_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diet_tracker_api.exception.MealNotFoundException;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.repository.MealDAO;

import lombok.RequiredArgsConstructor;

/*
 * Service in charge of handling all functional tasks to generate a Hello, World! message.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MealService {
    private final MealDAO mealDAO;

    /**
     * Looks for a potential Meal in the DAO and returns it.
     * Throws a MealNotFoundException if the element is not found.
     * 
     * @param id Meal id in the DB.
     * @return Matching Meal instance.
     * @throws MealNotFoundException if the id does not match any item.
     */
    private Meal findMealById(Long id) {
        return mealDAO.findById(id).orElseThrow(() -> new MealNotFoundException(id));
    }

    /**
     * Returns all registered Meal's as a List of MealOutDTO objects.
     * 
     * @return List of MealOutDTO representations of all Meals
     */
    @Transactional(readOnly = true)
    public List<Meal> getAllMeals() {
        return mealDAO.findAll();
    }

    /**
     * Looks for a potential Meal and returns it as a MealOutDTO object.
     * 
     * @param id Meal id in the DB.
     * @return Matching MealOutDTO representation of the matching instance.
     */
    @Transactional(readOnly = true)
    public Meal getMealById(Long id) {
        return findMealById(id);
    }

    /*
    */
    /**
     * Creates a new meal based on the input MealInDTO instance.
     * Returns the created Meal's MealOutDTO.
     * 
     * @param mealInDTO MealInDTO containing the information to be used.
     * @return MealOutDTO representation of the created instance.
     */
    public Meal createMeal(Meal meal) {
        return mealDAO.save(meal);
    }

    /**
     * Deletes the meal matching the provided Id.
     * 
     * @param id Meal id in the DB.
     */
    public void deleteMealById(Long id) {
        mealDAO.delete(findMealById(id));
    }

    /**
     * Edits an existing meal found based on the provided Id with the input
     * MealInDTO instance.
     * 
     * @param id        Meal id in the DB.
     * @param mealInDTO MealInDTO object containing the wanted new information.
     * @return MealOutDTO representation of the edited instance.
     * @throws MealNotFoundException if id does not match an existing Meal in the
     *                               DB.
     */
    public Meal editMealById(Long id, Meal meal) {
        // Checking that the provided id matches an instance in the DB.
        if (!mealDAO.existsById(id)) {
            throw new MealNotFoundException(id);
        }
        ;
        // The provided Meal does not contain an Id value (null).
        // By setting it, we are asking the DAO to override the existing item.
        meal.setId(id);
        return mealDAO.save(meal);
    }

}
