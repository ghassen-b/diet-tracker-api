package com.example.diet_tracker_api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
public class MealsEndpointIT extends KeycloakTestContainerIT {
    private RequestSpecification authenticatedRequestSpecification;
    private RequestSpecification anonymousRequestSpecification;

    @LocalServerPort
    private int port;

    private String createURLWithPort(String uri) {
        return "https://localhost:" + port + uri;
    }

    @BeforeEach
    void setupRequestSpecification() {
        anonymousRequestSpecification = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        authenticatedRequestSpecification = anonymousRequestSpecification
                .header("Authorization", "Bearer " + getToken());
    }

    @BeforeAll
    static void setupCommonItems() {
        // Relax HTTPS validation for all requests as we are using self-signed
        // certificates
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void shouldGetUnauthorized_WhenGetMealsWithoutAuthToken() {
        given()
                .when()
                .get(createURLWithPort("/meals"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldGetUnauthorized_WhenGetMealByIdWithoutAuthToken() {
        given()
                .when()
                .get(createURLWithPort("/meals/1"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldGetUnauthorized_WhenDeleteMealByIdWithoutAuthToken() {
        given()
                .when()
                .delete(createURLWithPort("/meals/1"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldGetUnauthorized_WhenEditMealByIdWithoutAuthToken() {
        given()
                .body(MealInDTO.builder().build())
                .when()
                .put(createURLWithPort("/meals/1"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldGetUnauthorized_WhenCreateMealByIdWithoutAuthToken() {
        given()
                .body(MealInDTO.builder().build())
                .when()
                .post(createURLWithPort("/meals/1"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldGet400_WhenUsingHTTPEndpoint() {
        Response response = given()
                .body(MealInDTO.builder().build())
                .when()
                .get("/api-docs"); // no https, testing with http
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        var expected = "This combination of host and port requires TLS.";
        assertTrue(response.getBody().asString().contains(expected));
    }

    @Test
    void shouldGetResults_WhenAccessingApiDocWithoutAuthToken() throws JSONException {
        given()
                .when()
                .get(createURLWithPort("/api-docs"))
                .then()
                .statusCode(HttpStatus.OK.value());
        given()
                .when()
                .get(createURLWithPort("/swagger-ui.html"))
                .then()
                .statusCode(HttpStatus.OK.value());
        given()
                .when()
                .get(createURLWithPort("/swagger-ui/index.html"))
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldGetStatus_WhenAccessingActuatorHealthWithoutAuTokeb() throws JSONException {
        given()
                .when()
                .get(createURLWithPort("/actuator/health"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", is("UP"));
    }

    @Test
    void shouldGetResults_WhenGetMealsWithToken() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1));
        var expected = "[ " +
                "{  " +
                "\"userId\": \"5669d3a8-edd4-4d9d-a737-7e9cb21fa974\", " +
                "\"mealDate\": \"1968-05-04\", " +
                "\"mealTime\": \"LUNCH\", " +
                "\"mealContent\": \"VEGETARIAN\" " +
                "} " +
                "]";
        JSONAssert.assertEquals(expected, response.getBody().asString(), false);
    }

    @Test
    void shouldGetItem_WhenGetMealByIdWithToken() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals/1"));
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo("5669d3a8-edd4-4d9d-a737-7e9cb21fa974"))
                .body("mealDate", equalTo("1968-05-04"))
                .body("mealTime", equalTo("LUNCH"))
                .body("mealContent", equalTo("VEGETARIAN"));
    }

    @Test
    @DirtiesContext
    void shouldGetItemId_WhenCreateMealWithToken() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .body(MealInDTO.builder()
                        .mealContent(MealContent.BEEF)
                        .mealTime(MealTime.DINNER)
                        .mealDate(LocalDate.of(2020, 11, 29))
                        .build())
                .when()
                .post(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body("size()", is(1)) // Only the Id is returned
                .body("id", is(3));

        // Get all meals to check that it's there
        response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(2));
    }

    @Test
    @DirtiesContext
    void shouldDeleteItem_WhenDeleteMealWithToken() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .when()
                .delete(createURLWithPort("/meals/1"));
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Get all meals to check that it's there
        response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(0));
    }

    @Test
    @DirtiesContext
    void shouldEditItem_WhenEditMealWithToken() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .body(MealInDTO.builder()
                        .mealContent(MealContent.BEEF)
                        .mealTime(MealTime.DINNER)
                        .mealDate(LocalDate.of(2020, 11, 29))
                        .build())
                .when()
                .put(createURLWithPort("/meals/1"));
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body("size()", is(1)) // Only the Id is returned
                .body("id", is(1));

        // Get all meals to check that we haven't created a new meal
        response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1));

        // Get the meal's content to check it
        response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals/1"));
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo("5669d3a8-edd4-4d9d-a737-7e9cb21fa974"))
                .body("mealDate", equalTo("2020-11-29"))
                .body("mealTime", equalTo("DINNER"))
                .body("mealContent", equalTo("BEEF"));
    }

    @Test
    void shouldGet404_WhenEditMealNotFound() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .body(MealInDTO.builder()
                        .mealContent(MealContent.BEEF)
                        .mealTime(MealTime.DINNER)
                        .mealDate(LocalDate.of(2020, 11, 29))
                        .build())
                .when()
                .put(createURLWithPort("/meals/42"));
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldGet404_WhenEditMealCreatedByOtherUser() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .body(MealInDTO.builder()
                        .mealContent(MealContent.LAMB)
                        .mealTime(MealTime.DINNER)
                        .mealDate(LocalDate.of(2020, 11, 29))
                        .build())
                .when()
                .put(createURLWithPort("/meals/2")); // This meal id belongs to another user
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldGet404_WhenGetMealByIdNotFound() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals/42"));
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldGet404_WhenGetMealOwnedByOtherUser() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals/2"));
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldGet400_WhenGetMealByIdWithIdFormat() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .when()
                .get(createURLWithPort("/meals/noAnInt"));
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("id", is("Invalid value for parameter 'id': noAnInt"));
    }

    @Test
    void shouldGet400_WhenCreateMealWithMissingField() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .body(MealInDTO.builder()
                        .build())
                .when()
                .post(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("mealTime", is("must not be null"))
                .body("mealContent", is("must not be null"))
                .body("mealDate", is("must not be null"));
    }

    @Test
    void shouldGet400_WhenCreateMealWithInvalidField() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .body("{" +
                        "\"mealDate\": \"notADate\"," +
                        "\"mealContent\": \"notAContent\"" +
                        "\"mealTime\": \"notATime\"" +
                        "}")
                .when()
                .post(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size()", is(1)) // The error raised only shows the first invalid field
                .body("mealDate", is("Invalid value for field 'mealDate': notADate"));
    }

    @Test
    void shouldGet400_WhenCreateMealWithMalFormedJson() throws JSONException {
        Response response = given(authenticatedRequestSpecification)
                .body("{" +
                        "\"anyField\": \"anything\"" +
                        "")
                .when()
                .post(createURLWithPort("/meals"));
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", is("Malformed JSON request"));
    }
}
