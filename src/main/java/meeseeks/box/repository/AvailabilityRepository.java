package meeseeks.box.repository;

import org.springframework.data.repository.CrudRepository;

import meeseeks.box.domain.AvailabilityEntity;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public interface AvailabilityRepository extends CrudRepository<AvailabilityEntity, Integer> { }
