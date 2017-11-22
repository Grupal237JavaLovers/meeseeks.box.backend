package meeseeks.box.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private SecurityConstants securityConstants;
    private UserService userService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   SecurityConstants securityConstants,
                                   UserService userService) {
        this.authenticationManager = authenticationManager;
        this.securityConstants = securityConstants;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            AccountCredentials credentials = new ObjectMapper()
                    .readValue(req.getInputStream(), AccountCredentials.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            new ArrayList<>()));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain,
                                            Authentication authentication) throws IOException, ServletException {
        String token = userService.getJWTToken((UserEntity) authentication.getPrincipal());
        response.addHeader(securityConstants.HEADER_STRING, securityConstants.TOKEN_PREFIX + token);
        response.setContentType("application/json");
        response.getWriter().println("{\"token\": \"Bearer " + token + "\"}");
    }
}
