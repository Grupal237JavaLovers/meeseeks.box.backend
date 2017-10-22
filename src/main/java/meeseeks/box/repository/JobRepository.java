package meeseeks.box.repository;

import meeseeks.box.domain.JobEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface JobRepository extends CrudRepository<JobEntity, Integer> {
    List<JobEntity> findAllByNameContaining(final @NotNull String name);
    List<JobEntity> findAllByLocationContaining(final @NotNull String name);
    List<JobEntity> findAllByPrice(final @NotNull Double price);
    List<JobEntity> findAllByPriceLessThanEqual(final @NotNull Double price);
    List<JobEntity> findAllByPriceGreaterThanEqual(final @NotNull Double price);
    List<JobEntity> findByType(final @NotNull String type);
    List<JobEntity> findByExpiration(final @NotNull Calendar expiration);
}
