package meeseeks.box.repository;

import meeseeks.box.domain.RequestEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface RequestRepository extends CrudRepository<RequestEntity, Integer> { }
