package com.example.diet_tracker_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DietTrackerApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
public class DietTrackerApiApplicationIT {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    public void testGetAllMeals() throws JSONException {
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        String expected = "["
                + "{\"mealEater\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@gmail.com\"},"
                + "\"mealDate\":\"1968-05-04\",\"mealTime\":\"LUNCH\",\"mealContent\":\"VEGETARIAN\"}"
                + "]";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testGetMealById() throws JSONException {
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        String expected = "{\"mealEater\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@gmail.com\"},"
                + "\"mealDate\":\"1968-05-04\",\"mealTime\":\"LUNCH\",\"mealContent\":\"VEGETARIAN\"}";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println(response.getBody());
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testGetMealById_GivenNonExistingMeal() throws JSONException {
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/42"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        String expected = "Meal with id=42 not found";

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expected, response.getBody());

    }

    @Test
    public void testDeleteMeal_GivenNotExistingMeal() throws JSONException {
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/42"),
                HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Meal with id=42 not found", response.getBody());

    }

    @Test
    @DirtiesContext
    public void testDeleteMeal() throws JSONException {
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void testPostMeal() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>(
                        "{\"userId\":1,\"mealDate\":\"1998-06-21\",\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        String expected = "{\"mealEater\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@gmail.com\"},"
                + "\"mealDate\":\"1998-06-21\",\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}";
        JSONAssert.assertEquals(expected, response.getBody(), true);

    }

    @Test
    public void testPostMeal_GivenWrongMediaType() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>("{\"toto\":1}", headers),
                String.class);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    public void testPostMeal_GivenUnknownUser() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>(
                        "{\"userId\":42,\"mealDate\":\"1998-06-21\",\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with id=42 not found", response.getBody());
    }

    /*
     * Checking that the primary error here is that the user is not found, not the
     * other missing fields.
     */
    @Test
    public void testPostMeal_GivenOnlyUnknownUser() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>("{\"userId\":42}", headers),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with id=42 not found", response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    public void testPostMeal_GivenNoUserField() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>(
                        "{\"mealDate\":\"1998-06-21\",\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().contains("Meal creation/update failed because of invalid input."));
        assertTrue(response.getBody().contains("userId field cannot be null"));
    }

    @SuppressWarnings("null")
    @Test
    public void testPostMeal_GivenInvalidDTO() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>("{\"userId\":1}", headers),
                String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().contains("Meal creation/update failed because of invalid input."));
        assertTrue(response.getBody().contains("interpolatedMessage='must not be null', propertyPath=mealContent"));
        assertTrue(response.getBody().contains("interpolatedMessage='must not be null', propertyPath=mealTime"));
        assertTrue(response.getBody().contains("interpolatedMessage='must not be null', propertyPath=mealDate"));
    }

    @SuppressWarnings("null")
    @Test
    public void testPostMeal_GivenInvalidDTOEnumValue() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>(
                        "{\"userId\":1,\"mealDate\":\"1998-06-21\",\"mealTime\":\"WHATEVER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Failed to read request"));

    }

    @Test
    @DirtiesContext
    public void testPutMeal() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.PUT,
                new HttpEntity<>(
                        "{\"userId\":1,\"mealDate\":\"1998-06-21\",\"mealTime\":\"LUNCH\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        String expected = "{\"mealEater\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@gmail.com\"},"
                + "\"mealDate\":\"1998-06-21\",\"mealTime\":\"LUNCH\",\"mealContent\":\"VEGAN\"}";
        JSONAssert.assertEquals(expected, response.getBody(), true);

    }

    @SuppressWarnings("null")
    @Test
    public void testPutMeal_GivenNullUserId() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.PUT,
                new HttpEntity<>(
                        "{\"mealTime\":\"LUNCH\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().contains("Meal creation/update failed because of invalid input."));
        assertTrue(response.getBody().contains("userId field cannot be null"));

    }

    @SuppressWarnings("null")
    @Test
    public void testPutMeal_GivenMissingFields() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.PUT,
                new HttpEntity<>(
                        "{\"userId\":1,\"mealTime\":\"LUNCH\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().contains("Meal creation/update failed because of invalid input."));

    }

}
