package com.example.diet_tracker_api;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.exception.UserNotFoundException;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.repository.UserDAO;

import lombok.RequiredArgsConstructor;

/**
 * Model MapperMapper bean provider class.
 */
@Configuration
@RequiredArgsConstructor
public class ModelMapperConfiguration {

    private final UserDAO userDAO;

    /**
     * Builds the model mapper bean with its custom configs.
     * 
     * @return ModelMapper instance.
     */
    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        // Customising the mapping from MealInDTO to Meal
        modelMapper.addMappings(new PropertyMap<MealInDTO, Meal>() {
            @Override
            protected void configure() {
                // We don't want to set the Id on the destination Meal entity
                // Since this field is marked as @GeneratedValue, it would be set to 1,
                // overriding the first existing value!
                skip(destination.getId());
                // The DTO provides the Id of the user. In the entity, we want to put the User
                // entity
                using(context -> userDAO.findById((Long) context.getSource())
                        .orElseThrow(() -> new UserNotFoundException((Long) context.getSource())))
                        .map(source.getMealEaterId(), destination.getMealEater());
            }
        });

        return modelMapper;

    }

}
