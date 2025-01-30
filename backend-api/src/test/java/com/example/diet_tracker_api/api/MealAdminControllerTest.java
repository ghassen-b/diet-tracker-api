package com.example.diet_tracker_api.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = { MealAdminController.class })
public class MealAdminControllerTest extends AbstractMealControllerTest {

    /**
     * All forbidden tests - for a non-admin user
     */

    @Test
    void whenGetUserMeals_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/meals")
                        .param("userId", mockUserId)
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenGetMealById_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/meals/42")
                        .param("userId", mockUserId)
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenPostMeal_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/admin/meals")
                        .param("userId", mockUserId)
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenDeleteMeal_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/admin/meals/42")
                        .param("userId", mockUserId)
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenEditMeal_givenJWTWithNoRole_thenForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/admin/meals/42")
                        .param("userId", mockUserId)
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(noRoleJwt))
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void whenGetUserMeals_givenValidJWT_thenMealsReturned() throws Exception {
        Mockito.when(mockMealService.getUserMeals(mockUserId)).thenReturn(List.of(meal1, meal2));

        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/meals")
                        .param("userId", mockUserId)
                        .with(adminJwt))
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
                MockMvcRequestBuilders.get("/admin/meals/" + mockMealId.toString())
                        .param("userId", mockUserId)
                        .with(adminJwt))
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
                MockMvcRequestBuilders.post("/admin/meals")
                        .param("userId", mockUserId)
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(adminJwt))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        JSONAssert.assertEquals(meal1IdStr, result.getResponse().getContentAsString(), false);
    }

    @Test
    void whenDeleteMeal_givenUserOwnsMeal_thenMealDeleted() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/admin/meals/" + mockMealId.toString())
                        .param("userId", mockUserId)
                        .with(adminJwt))
                .andExpectAll(
                        status().isNoContent());

        Mockito.verify(mockMealService).deleteMealById(mockUserId, mockMealId);
    }

    @Test
    void whenEditMeal_givenUserOwnsMeal_thenMealReturned() throws Exception {
        Mockito.when(mockMealService.editMealById(mockUserId, mockMealId,
                meal1)).thenReturn(meal1);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.put("/admin/meals/" + mockMealId.toString())
                        .param("userId", mockUserId)
                        .content(mealInDTOStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(adminJwt))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(meal1IdStr, result.getResponse().getContentAsString(), false);
    }
}
