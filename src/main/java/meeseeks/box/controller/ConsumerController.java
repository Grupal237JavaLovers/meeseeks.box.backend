package meeseeks.box.controller;

import javax.servlet.http.HttpServletResponse;

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
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.security.SecurityConstants;
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
    private final SecurityConstants securityConstants;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ConsumerController(final ConsumerRepository consumerRepository,
        UserService userService, SecurityConstants securityConstants
    ) {
        this.consumerRepository = consumerRepository;
        this.userService = userService;
        this.securityConstants = securityConstants;
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody ConsumerEntity getConsumerById(@PathVariable("id") final Integer id) throws NotFoundException {
        ConsumerEntity consumer = consumerRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Consumer not found");
        });

        return consumer;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerConsumer(@RequestBody @Validated(UserEntity.ValidationRegister.class) ConsumerEntity consumer) {
        LOGGER.info("Consumer " + consumer.getUsername() + " registers now ...");

        userService.saveUser(consumer);
    }
    
    @Secured({"ROLE_CONSUMER"})
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public void editConsumer(@RequestBody @Validated(UserEntity.ValidationEdit.class)
        ConsumerEntity consumer, Authentication auth, HttpServletResponse response
    ) {
        ConsumerEntity oldConsumer = (ConsumerEntity) auth.getPrincipal();

        if (consumer.getEmail() != null && !consumer.getEmail().isEmpty()) {
            oldConsumer.setEmail(consumer.getEmail());
        }
        if (consumer.getName() != null && !consumer.getName().isEmpty()) {
            oldConsumer.setName(consumer.getName());
        }
        if (consumer.getUsername() != null && !consumer.getUsername().isEmpty()) {
            oldConsumer.setUsername(consumer.getUsername());
        }
        if (consumer.getProfileImageUrl() != null && !consumer.getProfileImageUrl().isEmpty()) {
            oldConsumer.setProfileImageUrl(consumer.getProfileImageUrl());
        }

        consumerRepository.save(oldConsumer);

        response.addHeader(
            securityConstants.HEADER_STRING,
            securityConstants.TOKEN_PREFIX + userService.getJWTToken(oldConsumer)
        );
    }
}