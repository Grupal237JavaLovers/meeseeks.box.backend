package meeseeks.box.repository;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ConsumerRepository extends BaseCrudRepository<ConsumerEntity, Integer> {

    Optional<ConsumerEntity> findByJobsIsContaining(final JobEntity job);
}
