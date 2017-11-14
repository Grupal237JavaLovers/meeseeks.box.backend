package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.AccessDeniedException;
import meeseeks.box.model.ChangePasswordModel;
import meeseeks.box.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity currentUserName(Authentication auth) {
        return (UserEntity) auth.getPrincipal();
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.PATCH)
    public void changePassword(Authentication auth, @RequestBody @Valid ChangePasswordModel model) {
        UserEntity user = (UserEntity) auth.getPrincipal();
        if (!encoder.matches(model.getCurrentPassword(), user.getPassword())) {
            throw new AccessDeniedException("Current password not correct");
        }
        user.setPassword(model.getPassword());
        userService.saveUser(user);
    }

    @RequestMapping("/delete")
    public ResponseEntity delete() {
        UserEntity user =(UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.delete(user.getUsername()) ?
                new ResponseEntity<>(HttpStatus.ACCEPTED) :
                new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
