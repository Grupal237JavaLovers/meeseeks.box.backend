package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;



/**
 * @author Tiron Andreea-Ecaterina
 * @version 1.0
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/consumer")
public class ConsumerController {

    private final ConsumerRepository consumerRepository;

    private final UserService userService;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ConsumerController(final ConsumerRepository consumerRepository, UserService userService) {
        this.consumerRepository = consumerRepository;
        this.userService = userService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public void create() {
        consumerRepository.save(new ConsumerEntity("andrew_dale", "password", "Andrew Dale", "andrew.dale@gmail.com"));
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody ConsumerEntity getConsumerById(@PathVariable("id") final Integer id) {
        return consumerRepository.findOne(id);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerConsumer(@RequestBody @Valid ConsumerEntity consumer) {
        LOGGER.info("Provider " + consumer.getUsername() + " registers now ...");
        userService.saveUser(consumer);
    }
}