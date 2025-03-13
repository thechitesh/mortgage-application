package com.myorg.mortgage.service;

import com.myorg.mortgage.app.model.AmountDto;
import com.myorg.mortgage.app.model.InterestRateDto;
import com.myorg.mortgage.app.model.MortgageRequestDto;
import com.myorg.mortgage.app.model.MortgageResponseDto;
import com.myorg.mortgage.config.DataLoader;
import com.myorg.mortgage.model.InterestRate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;


class MortgageServiceTest {

    DataLoader dataLoader  = Mockito.mock();
    MortgageService service = new MortgageService(dataLoader);


    private static Stream<Arguments> provideInterestRates() {
        return Stream.of(
                Arguments.of(19, valueOf(2)),
                Arguments.of(16, valueOf(1.5)),
                Arguments.of(25, valueOf(2.5))
        );
    }

    private static Stream<Arguments> provideMortgageCalculationResponse() {
        return Stream.of(
                Arguments.of(buildMortgageRequest(valueOf(200), valueOf(200), valueOf(40), 10), buildResponse(false, null)),
                Arguments.of(buildMortgageRequest(valueOf(100), valueOf(200), valueOf(70), 10), buildResponse(false, null)),
                Arguments.of(buildMortgageRequest(valueOf(200), valueOf(200), valueOf(60), 10), buildResponse(true, BigDecimal.valueOf(1.75))),
                Arguments.of(buildMortgageRequest(valueOf(20000), valueOf(20000), valueOf(6000), 10), buildResponse(true, BigDecimal.valueOf(174.86)))
        );
    }


    @Test
    void test_GetInterestRates() throws IOException {
        Mockito.when(dataLoader.loadData()).thenReturn(getInterestRates());
        List<InterestRateDto> interestRates = service.getInterestRates();

        val dto1 = createDto(20, valueOf(2));
        val dto2 = createDto(15, valueOf(1.5));
        val dto3 = createDto(25, valueOf(2.5));
        val dto4 = createDto(10, valueOf(1));

        List<InterestRateDto> rateDtos = List.of(dto1, dto2, dto3, dto4);
        Assertions.assertThat(interestRates).hasSize(4)
                .isEqualTo(rateDtos);
    }

    @ParameterizedTest
    @MethodSource("provideInterestRates")
    void test_SearchNearestInterestRate(int maturityPeriod, BigDecimal expectedRate) {
        List<InterestRate> interestRates = getInterestRates();
        BigDecimal rate = service.searchNearestInterestRate(interestRates, maturityPeriod);
        Assertions.assertThat(rate).isEqualTo(expectedRate);
    }

    @ParameterizedTest
    @MethodSource("provideMortgageCalculationResponse")
    void test_monthlyCost(MortgageRequestDto request, MortgageResponseDto response) throws IOException {
        Mockito.when(dataLoader.loadData()).thenReturn(getInterestRates());
        MortgageResponseDto mortgageResponseDto = service.calculateMortgage(request);
        Assertions.assertThat(mortgageResponseDto).isEqualTo(response);
    }

    @Test
    void test_checkFeasibility_4timeIncome() {
        val request = buildMortgageRequest(valueOf(200), valueOf(200), valueOf(40), 10);
        MortgageResponseDto mortgageResponseDto = service.calculateMortgage(request);
        Assertions.assertThat(mortgageResponseDto.isFeasible()).isFalse();
    }

    @Test
    void test_checkFeasibility_GreaterThanHomeValue() {
        val request = buildMortgageRequest(valueOf(100), valueOf(200), valueOf(60), 10);
        MortgageResponseDto mortgageResponseDto = service.calculateMortgage(request);
        Assertions.assertThat(mortgageResponseDto.isFeasible()).isFalse();
    }

    @Test
    void test_checkFeasibility_CalculateMonthly() throws IOException {
        val request = buildMortgageRequest(valueOf(200), valueOf(200), valueOf(60), 10);
        Mockito.when(dataLoader.loadData()).thenReturn(getInterestRates());
        MortgageResponseDto mortgageResponseDto = service.calculateMortgage(request);
        Assertions.assertThat(mortgageResponseDto.isFeasible()).isTrue();

    }

    private static MortgageRequestDto buildMortgageRequest(BigDecimal homeValue, BigDecimal loanValue, BigDecimal income, int maturity) {
        val home = buildAmount(homeValue);
        val loan = buildAmount(loanValue);
        val inc = buildAmount(income);

        return MortgageRequestDto.builder()
                .homeValue(home)
                .loanValue(loan)
                .income(inc)
                .maturityPeriod(maturity)
                .build();
    }

    private static MortgageResponseDto buildResponse(boolean feasible, BigDecimal monthlyCost) {
        AmountDto monthly = null;
        if (monthlyCost != null) {
            monthly = buildAmount(monthlyCost);
        }

        return MortgageResponseDto.builder().feasible(feasible).monthlyCost(monthly).build();
    }


    private static AmountDto buildAmount(BigDecimal value) {
        return AmountDto.builder().value(value).currency("EUR").build();
    }

    private List<InterestRate> getInterestRates() {
        val interest1 = create(20, valueOf(2));
        val interest2 = create(15, valueOf(1.5));
        val interest3 = create(25, valueOf(2.5));
        val interest4 = create(10, valueOf(1));
        return List.of(interest1, interest2, interest3, interest4);
    }


    private InterestRate create(int maturity, BigDecimal rate) {
        return InterestRate.builder()
                .interestRate(rate)
                .maturityPeriod(maturity)
                .build();
    }

    private InterestRateDto createDto(int maturity, BigDecimal rate) {
        return InterestRateDto.builder()
                .interestRate(rate)
                .maturityPeriod(maturity)
                .build();
    }

}
