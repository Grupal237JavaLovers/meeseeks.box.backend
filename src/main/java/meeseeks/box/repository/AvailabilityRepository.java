package meeseeks.box.repository;

import meeseeks.box.domain.AvailabilityEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public interface AvailabilityRepository extends CrudRepository<AvailabilityEntity, Integer> { }
