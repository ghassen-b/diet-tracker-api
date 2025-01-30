package com.example.diet_tracker_api.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.diet_tracker_api.exception.MealNotFoundException;

@WebMvcTest(controllers = { MealUserController.class })
public class MealUserControllerTest extends AbstractMealControllerTest {
    /**
     * All forbidden tests - for user without the required role
     */

    @Test
    void whenGetUserMeals_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/meals")
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenGetMealById_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/meals/" + mockMealId.toString())
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenPostMeal_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/meals")
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenDeleteMeal_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/meals/" + mockMealId.toString())
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenEditMeal_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/meals/" + mockMealId.toString())
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    /**
     * Nominal test cases
     */

    @Test
    void whenGetUserMeals_givenValidJWT_thenMealsReturned() throws Exception {
        Mockito.when(mockMealService.getUserMeals(mockUserId)).thenReturn(List.of(meal1, meal2));

        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/meals")
                        .with(userJwt))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        var expected = "[ " + meal1OutStr + "," + meal2OutStr + "]";
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    void whenGetUserMealById_givenUserOwnsMeal_thenMealReturned() throws Exception {
        Mockito.when(mockMealService.getUserMealById(mockUserId, mockMealId)).thenReturn(meal1);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/meals/" + mockMealId.toString())
                        .with(userJwt))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        JSONAssert.assertEquals(meal1OutStr, result.getResponse().getContentAsString(), false);
    }

    @Test
    void whenCreateMeal_givenUserOwnsMeal_thenMealIdReturned() throws Exception {
        Mockito.when(mockMealService.createMeal(mockUserId, meal1)).thenReturn(meal1);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/meals")
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userJwt))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        JSONAssert.assertEquals(meal1IdStr, result.getResponse().getContentAsString(), false);
    }

    @Test
    void whenDeleteMeal_givenUserOwnsMeal_thenMealDeleted() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/meals/" + mockMealId.toString())
                        .with(userJwt))
                .andExpectAll(
                        status().isNoContent());

        Mockito.verify(mockMealService).deleteMealById(mockUserId, mockMealId);
    }

    @Test
    void whenEditMeal_givenUserOwnsMeal_thenMealReturned() throws Exception {
        Mockito.when(mockMealService.editMealById(mockUserId, mockMealId,
                meal1)).thenReturn(meal1);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.put("/meals/" + mockMealId.toString())
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userJwt))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(meal1IdStr, result.getResponse().getContentAsString(), false);
    }

    /**
     * Testing exceptions
     */
    @Test
    void whenGetUserMeals_givenMappingException_thenBadRequest() throws Exception {
        Mockito.when(mockMealService.getUserMeals(mockUserId)).thenThrow(new MappingException(List.of(new ErrorMessage("MSG"))));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/meals")
                        .with(userJwt))
                .andExpectAll(
                        status().isBadRequest());
    }

    
    @Test
    void whenGetUserMealById_givenMealNotFoundException_thenNotFound() throws Exception {
        Mockito.when(mockMealService.getUserMealById(mockUserId, mockMealId)).thenThrow(new MealNotFoundException(mockUserId, mockMealId));

        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/meals/" + mockMealId.toString())
                        .with(userJwt))
                .andExpectAll(
                        status().isNotFound())
                .andReturn();
        assertEquals(result.getResponse().getContentAsString(), "Meal with id=42 not found for userId=user");
    }

}
