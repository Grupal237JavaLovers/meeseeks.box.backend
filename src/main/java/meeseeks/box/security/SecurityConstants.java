package meeseeks.box.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SecurityConstants {

    @Value("${app.security.secret}")
    public String SECRET = "ThisIsASecret";

    public final long EXPIRATION_TIME = 7889238000L; // 3 months in miliseconds

    public final String TOKEN_PREFIX = "Bearer ";

    public final String HEADER_STRING = "Authorization";
}
