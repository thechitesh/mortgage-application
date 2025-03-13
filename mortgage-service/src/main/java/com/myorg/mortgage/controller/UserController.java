package com.myorg.mortgage.controller;

import com.myorg.mortgage.auth.api.UserApi;
import com.myorg.mortgage.auth.model.AuthenticationRequestDto;
import com.myorg.mortgage.auth.model.AuthenticationResponseDto;
import com.myorg.mortgage.auth.model.RegisterRequestDto;
import com.myorg.mortgage.mapper.UserMapper;
import com.myorg.mortgage.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<AuthenticationResponseDto> registerUser(RegisterRequestDto registerRequestDto) {
        val user = userMapper.toUser(registerRequestDto);
        val token = authenticationService.registerUser(user);
        return ResponseEntity.ok(AuthenticationResponseDto.builder().authToken(token).build());
    }

    @Override
    public ResponseEntity<AuthenticationResponseDto> login(AuthenticationRequestDto authenticationRequestDto) {
        String token = authenticationService.login(authenticationRequestDto.getEmail(), authenticationRequestDto.getPassword());
        return ResponseEntity.ok(AuthenticationResponseDto.builder().authToken(token).build());
    }
}
