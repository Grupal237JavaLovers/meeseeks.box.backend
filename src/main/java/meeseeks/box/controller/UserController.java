package meeseeks.box.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.AccessDeniedException;
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
    public UserController(BCryptPasswordEncoder encoder, UserService userService) {
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
}
