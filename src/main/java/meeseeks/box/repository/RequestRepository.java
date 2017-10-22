package meeseeks.box.repository;

import meeseeks.box.domain.RequestEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public interface RequestRepository extends CrudRepository<RequestEntity, Integer> { }
