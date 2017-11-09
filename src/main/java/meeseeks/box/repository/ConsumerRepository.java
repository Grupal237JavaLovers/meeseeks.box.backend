package meeseeks.box.repository;

import meeseeks.box.domain.ConsumerEntity;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ConsumerRepository extends BaseCrudRepository<ConsumerEntity, Integer> { }
