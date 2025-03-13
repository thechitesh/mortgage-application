package com.myorg.mortgage.mapper;

import com.myorg.mortgage.auth.model.RegisterRequestDto;
import com.myorg.mortgage.model.Role;
import com.myorg.mortgage.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(RegisterRequestDto dto) {
        return User.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.valueOf(dto.getRole()))
                .build();
    }

}
