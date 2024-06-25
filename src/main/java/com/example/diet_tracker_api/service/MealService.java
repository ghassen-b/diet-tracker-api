package com.example.diet_tracker_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.exception.MealInvalidInputException;
import com.example.diet_tracker_api.exception.MealNotFoundException;
import com.example.diet_tracker_api.mapper.MealMapper;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.repository.MealDAO;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

/*
 * Service in charge of handling all functional tasks to generate a Hello, World! message.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MealService {
    private final MealDAO mealDAO;

    private final MealMapper mealMapper;

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
    public List<MealOutDTO> getAllMeals() {
        List<Meal> listMeals = (List<Meal>) mealDAO.findAll();
        return mealMapper.fromEntity(listMeals);
    }

    /**
     * Looks for a potential Meal and returns it as a MealOutDTO object.
     * 
     * @param id Meal id in the DB.
     * @return Matching MealOutDTO representation of the matching instance.
     */
    @Transactional(readOnly = true)
    public MealOutDTO getMealById(Long id) {
        return mealMapper.fromEntity(findMealById(id));
    }

    /*
    */
    /**
     * Creates a new meal based on the input MealInDTO instance.
     * Returns the created Meal's MealOutDTO.
     * 
     * @param mealInDTO MealInDTO containing the information to be used.
     * @return MealOutDTO representation of the created instance.
     * @throws MealInvalidInputException if the provided input is not valid.
     */
    public MealOutDTO createMeal(MealInDTO mealInDTO) {
        try {
            Meal newMeal = mealDAO.save(mealMapper.fromInDTO(mealInDTO));
            return mealMapper.fromEntity(newMeal);
        } catch (ConstraintViolationException e) {
            throw new MealInvalidInputException(mealInDTO, e.getMessage());
        }
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
     */
    public MealOutDTO editMealById(Long id, MealInDTO mealInDTO) {
        return mealMapper.fromEntity(
                mealDAO.save(
                        mealMapper.editFromInDTO(
                                mealInDTO,
                                findMealById(id))));
    }

}
