package com.myorg.mortgage.security;

import com.myorg.mortgage.exception.MortgageAuthenticationException;
import com.myorg.mortgage.model.User;
import com.myorg.mortgage.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User saved = userRepository.save(user.toBuilder().password(encodedPassword).build());
        return jwtService.generateToken(saved);
    }

    public String login(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        return jwtService.generateToken(user);
    }


    public Boolean validateToken(String token) {
        String userEmail = jwtService.extractUserName(token);
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        return jwtService.isTokenValid(token, user);
    }


    public boolean isUserAuthenticated() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String headerAuth = request.getHeader("Authorization");
                return headerAuth != null && validateToken(headerAuth.substring(7));
            }
            return false;
        } catch (Exception exp) {
            throw new MortgageAuthenticationException("User is not Authenticated");
        }

    }

}
