package com.example.diet_tracker_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diet_tracker_api.exception.MealNotFoundException;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.repository.MealDAO;

import lombok.RequiredArgsConstructor;

/**
 * Service in charge of handling all functional tasks to generate a Hello, World! message.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MealService {
    /**
     * Meal DAO.
     */
    private final MealDAO mealDAO;

    /**
     * Looks for a potential Meal in the DAO and returns it.
     * Throws a MealNotFoundException if the element is not found.
     *
     * @param userId User id to whom the meal belongs
     * @param id     Meal id in the DB.
     * @return Matching Meal instance.
     * @throws MealNotFoundException if the id does not match any item.
     */
    private Meal findUserMealById(String userId, Long id) {
        return mealDAO.findByIdAndUserId(id, userId).orElseThrow(() -> new MealNotFoundException(userId, id));
    }

    /**
     * Returns all registered Meal's as a List of MealOutDTO objects.
     *
     * @param userId User id to whom the meal belongs
     * @return List of MealOutDTO representations of all Meals
     */
    @Transactional(readOnly = true)
    public List<Meal> getUserMeals(String userId) {
        return mealDAO.findByUserId(userId);
    }

    /**
     * Looks for a potential Meal and returns it as a MealOutDTO object.
     *
     * @param userId User id to whom the meal belongs
     * @param id     Meal id in the DB.
     * @return Matching MealOutDTO representation of the matching instance.
     */
    @Transactional(readOnly = true)
    public Meal getUserMealById(String userId, Long id) {
        return findUserMealById(userId, id);
    }

    /**
     * Creates a new meal based on the input MealInDTO instance.
     * Returns the created Meal's MealOutDTO.
     *
     * @param userId User id to whom the meal belongs
     * @param meal   Meal containing the information to be used.
     * @return MealOutDTO representation of the created instance.
     */
    public Meal createMeal(String userId, Meal meal) {
        meal.setUserId(userId);
        return mealDAO.save(meal);
    }

    /**
     * Deletes the meal matching the provided Id.
     *
     * @param userId Id of the user owning the meal.
     * @param id     Meal id in the DB.
     */
    public void deleteMealById(String userId, Long id) {
        mealDAO.delete(findUserMealById(userId, id));
    }

    /**
     * Edits an existing meal found based on the provided Id with the input
     * MealInDTO instance.
     *
     * @param userId Id of the user owning the meal.
     * @param id     Meal id in the DB.
     * @param meal   Meal object containing the wanted new information.
     * @return MealOutDTO representation of the edited instance.
     * @throws MealNotFoundException if id does not match an existing Meal in the
     *                               DB.
     */
    public Meal editMealById(String userId, Long id, Meal meal) {
        // Checking that the provided id & userId matches an instance in the DB.
        if (!mealDAO.existsByIdAndUserId(id, userId)) {
            throw new MealNotFoundException(userId, id);
        }
        // The provided Meal does not contain an Id value (null).
        // By setting it, we are asking the DAO to override the existing item.
        meal.setId(id);

        meal.setUserId(userId);
        return mealDAO.save(meal);
    }

}
