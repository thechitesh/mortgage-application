package com.myorg.mortgage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.mortgage.app.model.AmountDto;
import com.myorg.mortgage.app.model.InterestRateDto;
import com.myorg.mortgage.app.model.InterestRateResponseDto;
import com.myorg.mortgage.app.model.MortgageRequestDto;
import com.myorg.mortgage.app.model.MortgageResponseDto;
import com.myorg.mortgage.exception.GlobalExceptionHandler;
import com.myorg.mortgage.security.AuthenticationService;
import com.myorg.mortgage.service.MortgageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = MortgageController.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
//@WebMvcTest(controllers = MortgageController.class, secure = false)
//@WebMvcTest(MortgageController.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@Import(GlobalExceptionHandler.class)
class MortgageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MortgageService mortgageService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void test_GetInterestRates() throws Exception {
        when(authenticationService.isUserAuthenticated()).thenReturn(true);
        when(mortgageService.getInterestRates()).thenReturn(List.of(InterestRateDto.builder().interestRate(BigDecimal.ONE).build()));
        mockMvc.perform(get("/api/interest-rates").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.interestRates[0].interestRate").value(BigDecimal.ONE))
        ;
    }

    @Test
    public void test_CheckMortgage() throws Exception {
        AmountDto amount = new AmountDto (BigDecimal.valueOf(100), "EUR");
        MortgageRequestDto requestDto = new MortgageRequestDto(amount, 10, amount, amount);
        MortgageResponseDto responseDto = new MortgageResponseDto(true, new AmountDto(BigDecimal.ONE, "EUR"));

        when(authenticationService.isUserAuthenticated()).thenReturn(true);
        when(mortgageService.calculateMortgage(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feasible").value(true))
                .andExpect(jsonPath("$.monthlyCost.value").value(BigDecimal.ONE));
    }

}
