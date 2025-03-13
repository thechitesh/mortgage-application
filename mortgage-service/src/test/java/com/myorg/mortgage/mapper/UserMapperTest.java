package com.myorg.mortgage.mapper;

import com.myorg.mortgage.auth.model.RegisterRequestDto;
import com.myorg.mortgage.model.Role;
import com.myorg.mortgage.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    UserMapper mapper = new UserMapper();

    @Test
    void test_UserMapping() {
        RegisterRequestDto registerRequestDto = buildRequestDto();
        User user = mapper.toUser(registerRequestDto);
        assertThat(user).isEqualTo(buildUser());
    }

    private User buildUser() {
        return User.builder()
                .firstname("first-1")
                .lastname("last-1")
                .email("email-1")
                .password("pass-1")
                .role(Role.ROLE_USER)
                .build();
    }

    private RegisterRequestDto buildRequestDto() {
        return RegisterRequestDto.builder()
                .firstname("first-1")
                .lastname("last-1")
                .email("email-1")
                .password("pass-1")
                .role("ROLE_USER")
                .build();
    }

}
