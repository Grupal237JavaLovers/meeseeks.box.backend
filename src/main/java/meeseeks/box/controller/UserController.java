package meeseeks.box.controller;

import static java.util.Collections.emptyList;

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

import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.AccessDeniedException;
import meeseeks.box.exception.BadRequestException;
import meeseeks.box.model.ChangePasswordModel;
import meeseeks.box.service.PusherService;
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
    private final PusherService pusherService;

    @Autowired
    public UserController(final BCryptPasswordEncoder encoder, final UserService userService, final PusherService pusherService) {
        this.encoder = encoder;
        this.userService = userService;
        this.pusherService = pusherService;
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
    @PostMapping(value = "/auth/notifications", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, String> authNotifications(@RequestParam HashMap<?, ?> params)
    {
        String action = (String) params.get("action");

        if (!action.equals("READ")) {
            throw new BadRequestException();
        }

        return pusherService.authenticateUser((String) params.get("token"), (String) params.get("path"),
                action, "private-notifications"
        );
    }
}
