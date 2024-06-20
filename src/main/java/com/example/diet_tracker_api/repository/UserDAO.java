package com.example.diet_tracker_api.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.diet_tracker_api.model.User;

/**
 * DAO for users.
 */
public interface UserDAO extends CrudRepository<User, Long> {

}
