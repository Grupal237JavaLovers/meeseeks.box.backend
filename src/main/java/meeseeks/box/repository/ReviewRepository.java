package meeseeks.box.repository;

import meeseeks.box.domain.ReviewEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> { }
