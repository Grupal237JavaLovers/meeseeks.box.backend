package meeseeks.box.service;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.BadRequestException;
import meeseeks.box.security.SecurityConstants;

@Service
public class PusherService
{
    private static final Integer EXPIRE_TIME = 86400; // 24 hours in seconds
    private static final Integer LEEWAY_TIME = 600; // 10 minutes in seconds

    private final UserService userService;
    private final SecurityConstants securityConstants;

    @Value("${app.pusher.feeds.locator}")
    private String instanceLocator;

    @Value("${app.pusher.feeds.secret}")
    private String secret;

    private String appId;
    private String appKey;
    private String appSecret;

    @Autowired
    public PusherService(UserService userService, SecurityConstants securityConstants) {
        this.userService = userService;
        this.securityConstants = securityConstants;
    }

    @PostConstruct
    public void init() {
        String[] keys = instanceLocator.split("\\:");
        this.appId = keys[2];

        keys = secret.split("\\:");
        this.appKey = keys[0];
        this.appSecret = keys[1];
    }

    public HashMap<String, String> authenticateUser(String token, String path, String action, String channelPrefix) {
        // Check if the supplied user token is valid
        String username = Jwts.parser()
                .setSigningKey(securityConstants.SECRET.getBytes())
                .parseClaimsJws(token)
                .getBody().getSubject();

        if (username != null) {
            try {
                // Check that the channel the user tries to subscribe is his
                UserEntity user = (UserEntity) userService.loadUserByUsername(username);
                String[] paths = path.split("/");

                if (paths.length != 3 || !paths[1].equals(channelPrefix + "-" + user.getId())) {
                    throw new BadRequestException();
                }

                String newToken = this.generateAccessToken(path, action, user.getId());

                HashMap<String, String> map = new HashMap<>();

                map.put("access_token", newToken);
                map.put("token_type", "bearer");
                map.put("expires_in", EXPIRE_TIME.toString());

                return map;
            } catch (UsernameNotFoundException ex) {
                throw new BadRequestException();
            }
        }

        throw new BadRequestException();
    }


    private String generateAccessToken(String path, String action, Integer userId) {
        HashMap<String, Object> claims = new HashMap<>();
        HashMap<String, Object> claims2 = new HashMap<>();

        claims2.put("path", path);
        claims2.put("action", action);
        claims.put("permission", claims2);

        return Jwts.builder()
                .claim("app", this.appId)
                .setIssuer("api_keys/" + this.appKey)
                .setIssuedAt(new Date(System.currentTimeMillis() - LEEWAY_TIME * 100))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME * 100 - LEEWAY_TIME * 100))
                .setSubject(userId.toString())
                .claim("feeds", claims)
                .signWith(SignatureAlgorithm.HS512, this.appSecret.getBytes())
                .compact();
    }
}
