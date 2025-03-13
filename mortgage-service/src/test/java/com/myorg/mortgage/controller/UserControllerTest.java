package com.myorg.mortgage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.mortgage.auth.model.AuthenticationRequestDto;
import com.myorg.mortgage.auth.model.RegisterRequestDto;
import com.myorg.mortgage.mapper.UserMapper;
import com.myorg.mortgage.model.User;
import com.myorg.mortgage.security.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.yaml")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void test_RegisterUser() throws Exception {
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder().email("test@example.com").password("password").build();
        User user = User.builder().email("test@example.com").password("password").build();
        String token = "mockToken";

        when(userMapper.toUser(registerRequestDto)).thenReturn(user);
        when(authenticationService.registerUser(user)).thenReturn(token);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken").value(token));
    }

    @Test
    public void test_Login() throws Exception {
        AuthenticationRequestDto authenticationRequestDto = AuthenticationRequestDto.builder().email("test@example.com").password("password").build();
        String token = "mockToken";

        when(authenticationService.login(authenticationRequestDto.getEmail(), authenticationRequestDto.getPassword())).thenReturn(token);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken").value(token));
    }

}
