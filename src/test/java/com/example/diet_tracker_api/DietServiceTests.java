package com.example.diet_tracker_api;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

public class DietServiceTests {

    @Test
    void testGetHelloWorld() {
        var dietService = new DietService();
        assertInstanceOf(String.class, dietService.getHelloWorld().length());

    }
}
