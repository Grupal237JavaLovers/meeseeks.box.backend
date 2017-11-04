package meeseeks.box.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.UserEntity;
import meeseeks.box.repository.UserRepository;
import meeseeks.box.service.UserService;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody UserEntity index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (UserEntity) authentication.getPrincipal();
        }

        return userRepository.findByEmail("test@email.com")
                .orElseGet(this::generateAndSaveToDatabase);
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity currentUserName(Authentication auth) {
        return (UserEntity) auth.getPrincipal();
    }

    private UserEntity generateAndSaveToDatabase() {
        UserEntity user = new UserEntity("test@email.com", "user", "password", "Test Name");

        userService.saveUser(user);

        return user;
    }

}
