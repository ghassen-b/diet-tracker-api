package com.example.diet_tracker_api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.diet_tracker_api.dto.UserOutDTO;
import com.example.diet_tracker_api.model.User;

public class UserMapperTest {

    private UserMapper userMapper = new UserMapper();

    @Test
    void testFromEntity_givenUserEntity() {
        User user = User.builder()
                .id(42L)
                .firstName("toto")
                .lastName("tata")
                .email("a@b.com")
                .build();
        var output = userMapper.fromEntity(user);
        var expectedOutput = UserOutDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
        assertEquals(output, expectedOutput);
    }

    @Test
    void testFromEntity_givenListUserEntities() {
        User user1 = User.builder()
                .id(42L)
                .firstName("toto")
                .lastName("tata")
                .email("a@b.com")
                .build();
        User user2 = User.builder()
                .id(42L)
                .firstName("tutu")
                .lastName("titi")
                .email("c@d.com")
                .build();
        var outputList = userMapper.fromEntity(List.of(user1, user2));
        var expectedOutputList = List.of(
                UserOutDTO.builder()
                        .firstName(user1.getFirstName())
                        .lastName(user1.getLastName())
                        .email(user1.getEmail())
                        .build(),
                UserOutDTO.builder()
                        .firstName(user2.getFirstName())
                        .lastName(user2.getLastName())
                        .email(user2.getEmail())
                        .build());
        assertEquals(expectedOutputList, outputList);
    }
}
