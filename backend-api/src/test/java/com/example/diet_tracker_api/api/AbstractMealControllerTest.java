package com.example.diet_tracker_api.api;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.diet_tracker_api.dto.MealIdDTO;
import com.example.diet_tracker_api.dto.MealInDTO;
import com.example.diet_tracker_api.dto.MealOutDTO;
import com.example.diet_tracker_api.model.Meal;
import com.example.diet_tracker_api.model.MealContent;
import com.example.diet_tracker_api.model.MealTime;
import com.example.diet_tracker_api.service.MealService;

@EnableMethodSecurity(prePostEnabled = true)
public abstract class AbstractMealControllerTest {

    @MockitoBean
    MealService mockMealService;

    @MockitoBean
    ModelMapper mockModelMapper;

    @Autowired
    MockMvc mockMvc;

    JwtRequestPostProcessor adminJwt = SecurityMockMvcRequestPostProcessors.jwt()
            .authorities(new SimpleGrantedAuthority("DIET_APP_ADMIN"));
    JwtRequestPostProcessor userJwt = SecurityMockMvcRequestPostProcessors.jwt()
            .authorities(new SimpleGrantedAuthority("DIET_APP_USER"));
    JwtRequestPostProcessor noRoleJwt = SecurityMockMvcRequestPostProcessors.jwt();
    String mockUserId = "user";

    Meal meal1 = Meal.builder()
            .id(42L)
            .mealContent(MealContent.BEEF)
            .userId("someUser")
            .mealDate(LocalDate.of(1985, 5, 18))
            .mealTime(MealTime.BREAKFAST)
            .build();
    Meal meal2 = Meal.builder()
            .id(52L)
            .mealContent(MealContent.CHICKEN)
            .userId("someOtherUser")
            .mealDate(LocalDate.of(1885, 6, 18))
            .mealTime(MealTime.LUNCH)
            .build();
    MealIdDTO meal1IdDTO = new MealIdDTO(42L);
    MealOutDTO meal1Out = MealOutDTO.builder()
            .id(42L)
            .mealContent(MealContent.BEEF)
            .userId("someUser")
            .mealDate(LocalDate.of(1985, 5, 18))
            .mealTime(MealTime.BREAKFAST)
            .build();
    MealOutDTO meal2Out = MealOutDTO.builder()
            .id(52L)
            .mealContent(MealContent.CHICKEN)
            .userId("someOtherUser")
            .mealDate(LocalDate.of(1885, 6, 18))
            .mealTime(MealTime.LUNCH)
            .build();

    String meal1OutStr = "{  " +
            "\"id\": 42, " +
            "\"userId\": \"someUser\", " +
            "\"mealDate\": \"1985-05-18\", " +
            "\"mealTime\": \"BREAKFAST\", " +
            "\"mealContent\": \"BEEF\" " +
            "}";

    String meal2OutStr = "{  " +
            "\"id\": 52, " +
            "\"userId\": \"someOtherUser\", " +
            "\"mealDate\": \"1885-06-18\", " +
            "\"mealTime\": \"LUNCH\", " +
            "\"mealContent\": \"CHICKEN\" " +
            "} ";

    String mealInDTOStr = "{  " +
            "\"mealDate\": \"1885-06-18\", " +
            "\"mealTime\": \"LUNCH\", " +
            "\"mealContent\": \"CHICKEN\" " +
            "} ";

    MealInDTO mealInDTO = MealInDTO.builder()
            .mealDate(LocalDate.of(1885, 6, 18))
            .mealTime(MealTime.LUNCH)
            .mealContent(MealContent.CHICKEN)
            .build();

    String meal1IdStr = "{  " +
            "\"id\": 42" +
            "}";

    MealInDTO mockMealIn = Mockito.mock(MealInDTO.class);
    Long mockMealId = 42L;

    @BeforeEach
    void setupCommonMocks() {
        Mockito.when(mockModelMapper.map(meal1, MealOutDTO.class)).thenReturn(meal1Out);
        Mockito.when(mockModelMapper.map(meal2, MealOutDTO.class)).thenReturn(meal2Out);
        Mockito.when(mockModelMapper.map(meal1, MealIdDTO.class)).thenReturn(meal1IdDTO);
        Mockito.when(mockModelMapper.map(mealInDTO, Meal.class)).thenReturn(meal1);
    }
}
