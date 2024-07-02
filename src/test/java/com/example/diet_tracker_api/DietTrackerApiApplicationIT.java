package com.example.diet_tracker_api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;
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
    public void testGetMealById_GivenWrongMealIdType() throws JSONException {
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1notalong"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        String expected = "{\"id\":\"Invalid value for parameter 'id': 1notalong\"}";

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        // Saving the previous state of GET /meals
        var initialGetAllMealsJsonArray = (JSONArray) JSONParser.parseJSON(restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class).getBody());

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>(
                        "{\"mealEaterId\":1,\"mealDate\":\"1998-06-21\",\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        String expected = "{\"mealEater\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@gmail.com\"},"
                + "\"mealDate\":\"1998-06-21\",\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}";
        JSONAssert.assertEquals(expected, response.getBody(), true);

        // Making here now that the new item was really created, not overriding an
        // existing one
        var newGetAllMealsJsonArray = (JSONArray) JSONParser.parseJSON(restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class).getBody());
        
        assertEquals(initialGetAllMealsJsonArray.length() + 1, newGetAllMealsJsonArray.length());

        JSONAssert.assertEquals(expected,
                newGetAllMealsJsonArray.get(newGetAllMealsJsonArray.length() - 1).toString(), true);

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
                        "{\"mealEaterId\":42,\"mealDate\":\"1998-06-21\",\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with id=42 not found", response.getBody());
    }

    @Test
    public void testPostMeal_GivenMissingFields() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>("{\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(
                "{\"mealEaterId\":\"must not be null\"," +
                        "\"mealDate\":\"must not be null\"}",
                response.getBody(), true);
    }

    @Test
    public void testPostMeal_GivenInvalidDTOEnumValue() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>(
                        "{\"mealEaterId\":1,\"mealDate\":\"1998-06-21\"," +
                                "\"mealTime\":\"IDONTEXIST\",\"mealContent\":\"IAMNOTCONSIDERED\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(
                "{\"mealTime\":\"Invalid value for field 'mealTime': IDONTEXIST\"}",
                response.getBody(), true);

    }

    @Test
    public void testPostMeal_GivenMalformedJSON() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals"),
                HttpMethod.POST,
                new HttpEntity<>(
                        "{toto}",
                        headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(
                "{\"error\": \"Malformed JSON request\"}",
                response.getBody(), true);

    }

    @Test
    @DirtiesContext
    public void testPutMeal() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.PUT,
                new HttpEntity<>(
                        "{\"mealEaterId\":1,\"mealDate\":\"1998-06-21\",\"mealTime\":\"LUNCH\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        String expected = "{\"mealEater\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@gmail.com\"},"
                + "\"mealDate\":\"1998-06-21\",\"mealTime\":\"LUNCH\",\"mealContent\":\"VEGAN\"}";
        JSONAssert.assertEquals(expected, response.getBody(), true);

    }

    @Test
    public void testPutMeal_GivenMissingFields() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.PUT,
                new HttpEntity<>("{\"mealTime\":\"DINNER\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(
                "{\"mealEaterId\":\"must not be null\"," +
                        "\"mealDate\":\"must not be null\"}",
                response.getBody(), true);
    }

    @Test
    public void testPutMeal_GivenInvalidDTOEnumValue() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/1"),
                HttpMethod.PUT,
                new HttpEntity<>(
                        "{\"mealEaterId\":1,\"mealDate\":\"1998-06-21\"," +
                                "\"mealTime\":\"IDONTEXIST\",\"mealContent\":\"IAMNOTCONSIDERED\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(
                "{\"mealTime\":\"Invalid value for field 'mealTime': IDONTEXIST\"}",
                response.getBody(), true);

    }

    @Test
    public void testPutMeal_GivenUnknownMealId() throws JSONException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/meals/42"),
                HttpMethod.PUT,
                new HttpEntity<>(
                        "{\"mealEaterId\":1,\"mealDate\":\"1998-06-21\",\"mealTime\":\"LUNCH\",\"mealContent\":\"VEGAN\"}",
                        headers),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Meal with id=42 not found", response.getBody());
    }

}
