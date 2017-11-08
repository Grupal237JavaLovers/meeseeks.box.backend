package meeseeks.box.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import meeseeks.box.domain.UserEntity;
import meeseeks.box.service.UserService;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private SecurityConstants securityConstants;
    private UserService userService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
        SecurityConstants securityConstants, UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.securityConstants = securityConstants;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            AccountCredentials creds = new ObjectMapper()
                    .readValue(req.getInputStream(), AccountCredentials.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain,
        Authentication auth
    ) throws IOException, ServletException {
        String token = userService.getJWTToken((UserEntity) auth.getPrincipal());

        res.addHeader(securityConstants.HEADER_STRING, securityConstants.TOKEN_PREFIX + token);
    }
}
