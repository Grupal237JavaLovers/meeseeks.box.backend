package meeseeks.box.controller;

import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.AccessDeniedException;
import meeseeks.box.exception.BadRequestException;
import meeseeks.box.model.ChangePasswordModel;
import meeseeks.box.service.PusherService;
import meeseeks.box.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.emptyList;

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
    public UserController(
            final BCryptPasswordEncoder encoder,
            final UserService userService,
            final PusherService pusherService) {
        this.encoder = encoder;
        this.userService = userService;
        this.pusherService = pusherService;
    }

    @ResponseBody
    @GetMapping("/current")
    public UserEntity currentUserName(Authentication auth) {
        return (UserEntity) auth.getPrincipal();
    }

    @ResponseBody
    @Secured({"ROLE_USER"})
    @PatchMapping("/change-password")
    public ResponseEntity<UserEntity> changePassword(
            @RequestBody @Valid ChangePasswordModel model,
            @AuthenticationPrincipal @ApiIgnore UserEntity user) {
        if (!model.getCurrentPassword().equals(user.getPassword())) {
            throw new AccessDeniedException("Current password not correct");
        }
        user.setPassword(model.getPassword());
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.ACCEPTED);
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
    public List<UserEntity> findUsersByNameOrEmail(
            @RequestParam(required = false, value = "name") final String name,
            @RequestParam(required = false, value = "email") final String email,
            @PathVariable("limit") final Integer limit) {
        return name != null ? userService.findUsersByNameContaining(name, limit) :
                email != null ? userService.findUsersByEmailContaining(email, limit) :
                        emptyList();
    }

    @ResponseBody
    @PostMapping(value = "/auth/notifications",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, String> authNotifications(
            @RequestParam HashMap<?, ?> params) {
        String action = (String) params.get("action");
        if (!action.equals("READ")) {
            throw new BadRequestException();
        }
        return pusherService.authenticateUser(
                (String) params.get("token"),
                (String) params.get("path"),
                action, "private-notifications"
        );
    }
}
