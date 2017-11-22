package meeseeks.box.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import meeseeks.box.repository.UserRepository;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private SecurityConstants securityConstants;
    private UserRepository userRepository;

    private final Logger LOGGER = Logger.getLogger(JWTAuthorizationFilter.class.getName());
    
    public JWTAuthorizationFilter(AuthenticationManager authManager, SecurityConstants securityConstants, UserRepository userRepo) {
        super(authManager);
        this.securityConstants = securityConstants;
        this.userRepository = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String header = request.getHeader(securityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(securityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(securityConstants.HEADER_STRING);
        if (token != null) {
            try {
                // parse the token.
                String username = Jwts.parser()
                        .setSigningKey(securityConstants.SECRET.getBytes())
                        .parseClaimsJws(token.replace(securityConstants.TOKEN_PREFIX, ""))
                        .getBody().getSubject();
                if (username != null) {
                    return userRepository.findByUsername(username)
                            .map(user -> new UsernamePasswordAuthenticationToken(user, user.getConfirmPassword(), user.getAuthorities())).orElse(null);
                }
            } catch (Exception ex) {
                LOGGER.error("Invalid JWT: " + ex.getMessage());
            }
            return null;
        }
        return null;
    }
}
