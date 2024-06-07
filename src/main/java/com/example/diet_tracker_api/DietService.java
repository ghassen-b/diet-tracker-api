package com.example.diet_tracker_api;

import org.springframework.stereotype.Service;

/*
 * Service in charge of handling all functional tasks to generate a Hello, World! message.
 */
@Service
public class DietService {

    public String getHelloWorld() {
        return "Hello, World!\n";
    }

}
