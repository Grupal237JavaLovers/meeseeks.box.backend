package meeseeks.box.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.service.UserService;



/**
 * @author Tiron Andreea-Ecaterina
 * @version 1.0
 */

@RestController
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

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody ConsumerEntity getConsumerById(@PathVariable("id") final Integer id) throws NotFoundException {
        ConsumerEntity consumer = consumerRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Consumer not found");
        });

        return consumer;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerConsumer(@RequestBody @Valid ConsumerEntity consumer) {
        LOGGER.info("Consumer " + consumer.getUsername() + " registers now ...");

        userService.saveUser(consumer);
    }
    
    @Secured({"ROLE_CONSUMER"})
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public void editConsumer(@RequestBody @Validated() ConsumerEntity consumer, Authentication auth) {
        ConsumerEntity oldConsumer = (ConsumerEntity) auth.getPrincipal();

        oldConsumer.setEmail(consumer.getEmail());

        if (consumer.getPassword() != null) {
            oldConsumer.setPassword(consumer.getPassword());
        }

        oldConsumer.setName(consumer.getName());
        oldConsumer.setUsername(consumer.getUsername());
        oldConsumer.setProfileImageUrl(consumer.getProfileImageUrl());

        userService.saveUser(oldConsumer);
    }
}