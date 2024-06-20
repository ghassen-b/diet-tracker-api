package com.example.diet_tracker_api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.diet_tracker_api.dto.UserOutDTO;
import com.example.diet_tracker_api.model.User;

@Component
public class UserMapper {
    /**
     * Converts a User instance into a UserOutDTO instance.
     * 
     * @param user User instance to convert.
     * @return UserOutDTO representation.
     */
    public UserOutDTO fromEntity(User user) {
        return UserOutDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    /**
     * Converts a List of User instances into a List of UserOutDTO instances.
     * 
     * @param listUsers List of User instances to convert.
     * @return List of matching UserOutDTO representations.
     */
    public List<UserOutDTO> fromEntity(List<User> listUsers) {
        return listUsers.stream().map(this::fromEntity).toList();
    }

}
