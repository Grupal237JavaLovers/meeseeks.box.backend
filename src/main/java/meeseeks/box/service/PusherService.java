package meeseeks.box.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private static final String PUSHER_ENDPOINT = "https://us1.pusherplatform.io/services/feeds/v1/";

    private final UserService userService;
    private final SecurityConstants securityConstants;
    private final RestTemplate rest;

    @Value("${app.pusher.feeds.locator}")
    private String instanceLocator;

    @Value("${app.pusher.feeds.secret}")
    private String secret;

    private String appId;
    private String appKey;
    private String appSecret;

    private final Logger LOGGER = Logger.getLogger(PusherService.class.getName());

    @Autowired
    public PusherService(UserService userService, SecurityConstants securityConstants, RestTemplateBuilder restBuilder) {
        this.userService = userService;
        this.securityConstants = securityConstants;
        this.rest = restBuilder.build();
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


    public void createNotification(NotificationType jobApply, Integer userId, HashMap<String,Object> data) {
        data.put("type", jobApply);

        HashMap<String, Object> items = new HashMap<>();
        List<Object> item = new ArrayList<>();
        item.add(data);

        items.put("items", item);

        HttpHeaders headers = new HttpHeaders(){{
            set("Authorization", "Bearer " + generateAccessToken("feeds/private-notifications-" + userId + "/items", "WRITE", userId));
        }};

        try {
            this.rest.exchange(PUSHER_ENDPOINT + this.appId + "/feeds/private-notifications-" + userId + "/items", HttpMethod.POST, new HttpEntity<>(items, headers), String.class);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    private String generateAccessToken(String path, String action, Integer userId) {
        HashMap<String, Object> claims = new HashMap<>();
        HashMap<String, Object> permission = new HashMap<>();

        permission.put("path", path);
        permission.put("action", action);
        claims.put("permission", permission);

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

    public enum NotificationType {
        JOB_APPLY, JOB_ACCEPT
    }
}
