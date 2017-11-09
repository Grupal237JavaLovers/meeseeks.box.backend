package meeseeks.box.repository;

import meeseeks.box.domain.ReviewEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> { }
