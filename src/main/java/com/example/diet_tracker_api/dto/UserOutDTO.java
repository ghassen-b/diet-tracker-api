package com.example.diet_tracker_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/*
 * DTO used when describing a User instance as a response.
 */
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class UserOutDTO {

    private String firstName;

    private String lastName;

    private String email;

}
