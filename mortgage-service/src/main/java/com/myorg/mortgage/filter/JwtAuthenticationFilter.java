package com.myorg.mortgage.filter;

import com.myorg.mortgage.security.JwtService;
import com.myorg.mortgage.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        final String userEmail;
        try {

            if (!StringUtils.isEmpty(authHeader) && StringUtils.startsWith(authHeader, "Bearer ")) {
//                filterChain.doFilter(request, response);

                String jwt = authHeader.substring(7);
                log.debug("JWT - {}", jwt);
                userEmail = jwtService.extractUserName(jwt);
                if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        log.debug("User - {}", userDetails);
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                    }
                }
            }
        } catch (JwtException jwtException) {
            request.setAttribute("expires", jwtException);
        } catch (Exception exception) {
            request.setAttribute("filter.exception", exception);
        }
        filterChain.doFilter(request, response);
    }

}
