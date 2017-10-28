package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.dto.ConsumerDto;
import meeseeks.box.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tiron Andreea-Ecaterina
 * @version 1.0
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/consumer")
public class ConsumerController {

    private final ConsumerRepository consumerRepository;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ConsumerController(final ConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;
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
    public void registerConsumer(@RequestBody @Valid ConsumerDto consumerDto) {
        LOGGER.log(Level.INFO, "Provider {0} try to register!", consumerDto.getUsername());
        //consumerRepository.save(new ConsumerEntity("kate_middleton", "password", "Kate Middleton", "kate.middleton@gmail.com"));
    }
}