package meeseeks.box.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.repository.UserRepository;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private SecurityConstants securityConstants;
    private UserRepository userRepo;

    public JWTAuthorizationFilter(AuthenticationManager authManager, SecurityConstants securityConstants, UserRepository userRepo) {
        super(authManager);
        this.securityConstants = securityConstants;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(securityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(securityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(securityConstants.HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(securityConstants.SECRET.getBytes())
                    .parseClaimsJws(token.replace(securityConstants.TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (user != null) {
                UserEntity entity = userRepo.findByUsername(user);

                if (entity != null) {
                    return new UsernamePasswordAuthenticationToken(entity, entity.getPassword(), entity.getAuthorities());
                }
            }

            return null;
        }
        return null;
    }
}
