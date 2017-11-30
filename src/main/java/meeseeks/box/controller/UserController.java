package meeseeks.box.controller;

import static java.util.Collections.emptyList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.AccessDeniedException;
import meeseeks.box.exception.BadRequestException;
import meeseeks.box.model.ChangePasswordModel;
import meeseeks.box.service.UserService;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@RequestMapping("/user")
public class UserController {

    private final BCryptPasswordEncoder encoder;
    private final UserService userService;

    @Autowired
    public UserController(final BCryptPasswordEncoder encoder, final UserService userService) {
        this.encoder = encoder;
        this.userService = userService;
    }

    @ResponseBody
    @GetMapping("/current")
    public UserEntity currentUserName(Authentication auth) {
        return (UserEntity) auth.getPrincipal();
    }

    @PatchMapping("/change-password")
    public void changePassword(Authentication auth, @RequestBody @Valid ChangePasswordModel model) {
        UserEntity user = (UserEntity) auth.getPrincipal();
        if (!encoder.matches(model.getCurrentPassword(), user.getPassword())) {
            throw new AccessDeniedException("Current password not correct");
        }
        user.setPassword(model.getPassword());
        userService.saveUser(user);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.delete(user.getUsername()) ?
                new ResponseEntity<>(HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    @GetMapping("/find/{limit}")
    public List<UserEntity> findUsersBy(@RequestParam(required = false, value = "name") final String name,
                                        @RequestParam(required = false, value = "email") final String email,
                                        @PathVariable("limit") final Integer limit) {
        return name != null ? userService.findUsersByNameContaining(name, limit) :
                email != null ? userService.findUsersByEmailContaining(email, limit) : emptyList();
    }

    @ResponseBody
    @PostMapping(value = "/auth/notifications", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<?, ?> authNotifications(@RequestParam HashMap<?, ?> params)
    {
        String action = (String) params.get("action");

        if (!action.equals("READ")) {
            throw new BadRequestException();
        }

        String token = (String) params.get("token");
        String path = (String) params.get("path");

        String newToken = this.generateAccessToken(path, action);

        HashMap<String, Object> map = new HashMap<>();

        map.put("access_token", newToken);
        map.put("token_type", "bearer");
        map.put("expires_in", "" + (24 * 60 * 60));

        return map;
    }

    private String generateAccessToken(String path, String action) {
        HashMap<String, Object> claims = new HashMap<>();
        HashMap<String, Object> claims2 = new HashMap<>();

        claims2.put("path", path);
        claims2.put("action", action);
        claims.put("permission", claims2);

        return Jwts.builder()
                .claim("app", "402dd0d4-06b3-44bd-b39f-600839531748")
                .setIssuer("api_keys/" + "6a80dc5a-e240-4429-80a0-74289802f10d")
                .setIssuedAt(new Date(System.currentTimeMillis() - 60 * 10 * 100))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 100 - 60 * 10 * 100))
                .setSubject(4 + "")
                .claim("feeds", claims)
                .signWith(SignatureAlgorithm.HS512, "CSOjERDCT4Z4klIazvqUKHNAlc7WnyJFtVAT2EeSxTA=".getBytes())
                .compact();
    }
}
