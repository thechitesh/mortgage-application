package com.myorg.mortgage.controller;

import com.myorg.mortgage.app.api.ApiApi;
import com.myorg.mortgage.app.model.InterestRateDto;
import com.myorg.mortgage.app.model.InterestRateResponseDto;
import com.myorg.mortgage.app.model.MortgageRequestDto;
import com.myorg.mortgage.app.model.MortgageResponseDto;
import com.myorg.mortgage.security.AuthenticationService;
import com.myorg.mortgage.service.MortgageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MortgageController implements ApiApi {

    private final MortgageService mortgageService;
    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<InterestRateResponseDto> getMortgageInterestRate() {
        authenticationService.isUserAuthenticated();
        List<InterestRateDto> rateDTOs = mortgageService.getInterestRates();
        return ResponseEntity.ok(InterestRateResponseDto.builder().interestRates(rateDTOs).build());
    }

    @Override
    public ResponseEntity<MortgageResponseDto> checkMortgage(MortgageRequestDto mortgageRequestDto) {
        authenticationService.isUserAuthenticated();
        return ResponseEntity.ok(mortgageService.calculateMortgage(mortgageRequestDto));
    }

}
