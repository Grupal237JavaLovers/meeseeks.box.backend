package meeseeks.box.controller;

import meeseeks.box.domain.UserEntity;
import meeseeks.box.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody UserEntity index() {
        return userRepository.findByEmail("test@email.com")
                .orElseGet(this::generateAndSaveToDatabase);
    }

    private UserEntity generateAndSaveToDatabase() {
        UserEntity user = new UserEntity("test@email.com", "password", "firstName", "lastName");
        userRepository.save(user);
        return user;
    }

}
