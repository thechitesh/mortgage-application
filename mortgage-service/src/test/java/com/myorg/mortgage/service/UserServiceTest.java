package com.myorg.mortgage.service;

import com.myorg.mortgage.model.User;
import com.myorg.mortgage.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/*@DataJpaTest
@SpringBootConfiguration
@EnableTransactionManagement
@EntityScan("nl.ing.securities.exams.modules.model")
@EnableJpaRepositories("com.myorg.mortgage.repository")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")*/
class UserServiceTest {


    private final UserRepository userRepository = Mockito.mock();

    private final UserService userService = new UserService(userRepository);

    @Test
    void test_loadUserByUsername() {
        String email = "user-email";
        User user = User.builder().firstname("name-1").email(email).build();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetailsService userDetailsService = userService.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Assertions.assertThat(userDetails)
                .isNotNull()
                .isInstanceOf(User.class)
                .hasFieldOrPropertyWithValue("email", email);
    }



}
