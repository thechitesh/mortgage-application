package com.myorg.mortgage.service;

import com.myorg.mortgage.app.model.AmountDto;
import com.myorg.mortgage.app.model.InterestRateDto;
import com.myorg.mortgage.app.model.MortgageRequestDto;
import com.myorg.mortgage.app.model.MortgageResponseDto;
import com.myorg.mortgage.config.DataLoader;
import com.myorg.mortgage.exception.MortgageException;

import com.myorg.mortgage.model.InterestRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MortgageService {

    private final DataLoader dataLoader;

    public List<InterestRateDto> getInterestRates() {
        try {
            ModelMapper mapper = new ModelMapper();
            var  rates = dataLoader.loadData();
            return rates.stream()
                    .map(rate -> mapper.map(rate, InterestRateDto.class))
                    .toList();
        } catch (IOException ioException) {
            throw new MortgageException("Error while loading interest rates", ioException);
        }
    }

    public MortgageResponseDto calculateMortgage(MortgageRequestDto requestDto) {
        boolean feasible = validateIncome(requestDto.getIncome(), requestDto.getLoanValue()) && validateMortgageAmount(requestDto.getHomeValue(), requestDto.getLoanValue());
        if (feasible) {
            AmountDto monthlyAmount = calculateMonthlyAmount(requestDto);
            return MortgageResponseDto.builder().feasible(true).monthlyCost(monthlyAmount).build();
        }
        return MortgageResponseDto.builder().feasible(false).build();
    }


    private boolean validateMortgageAmount(AmountDto homeValue, AmountDto loanValue) {
        return loanValue.getValue().compareTo(homeValue.getValue()) <= 0;
    }

    private boolean validateIncome(AmountDto income, AmountDto loanValue) {
        return loanValue.getValue().compareTo(income.getValue().multiply(BigDecimal.valueOf(4))) <= 0;
    }

    private AmountDto calculateMonthlyAmount(MortgageRequestDto requestDto) {
        try {
            List<InterestRate> interestRates = dataLoader.loadData();
            BigDecimal loanValue = requestDto.getLoanValue().getValue();
            int maturityInMonths = requestDto.getMaturityPeriod() * 12;
            BigDecimal annualInterestRate = searchNearestInterestRate(interestRates, requestDto.getMaturityPeriod());
            log.debug("annual interest rate {}", annualInterestRate);
            BigDecimal monthlyPayment = monthlyAmount(annualInterestRate, loanValue, maturityInMonths);
            log.info("Monthly payment {}", monthlyPayment);
            return AmountDto.builder().value(monthlyPayment).currency(requestDto.getLoanValue().getCurrency()).build();
        } catch (Exception exception) {
            throw new MortgageException("Error while loading interest rates", exception);
        }
    }

    private static BigDecimal monthlyAmount(BigDecimal annualInterestRate, BigDecimal loanValue, int months) {
        BigDecimal monthlyInterest = annualInterestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
        BigDecimal numerator = loanValue.multiply(monthlyInterest).multiply(monthlyInterest.add(BigDecimal.ONE).pow(months));
        BigDecimal denominator = (BigDecimal.ONE.add(monthlyInterest).pow(months)).subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    // search the closest maturity date for interest rate
    protected BigDecimal searchNearestInterestRate(List<InterestRate> interestRates, int maturityPeriod) {
        List<InterestRate> sortedList = interestRates.stream().sorted().toList();
        InterestRate targetMaturity = InterestRate.builder().maturityPeriod(maturityPeriod).build();
        int index = Collections.binarySearch(sortedList, targetMaturity, Comparator.comparing(InterestRate::getMaturityPeriod));
        if (index >= 0) {
            return sortedList.get(index).getInterestRate(); // Exact match found
        } else {
            index = -index - 1; // Calculate insertion point
            if (index == 0) {
                return sortedList.get(0).getInterestRate(); // Nearest element is the first element
            } else if (index == sortedList.size()) {
                return sortedList.get(sortedList.size() - 1).getInterestRate(); // Nearest element is the last element
            } else {
                InterestRate lower = sortedList.get(index - 1);
                InterestRate higher = sortedList.get(index);
                return (maturityPeriod - lower.getMaturityPeriod() <= higher.getMaturityPeriod() - maturityPeriod) ? lower.getInterestRate() : higher.getInterestRate();
            }
        }
    }

}
