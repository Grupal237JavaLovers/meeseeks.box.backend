package meeseeks.box.repository;

import java.util.Calendar;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;

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
    @Query("select j from JobEntity j join j.requests r join r.provider p where p=?1 order by j.created desc")
    List<JobEntity> findLatestJobByProvider(final @NotNull ProviderEntity provider, Pageable pageable);
    @Query("select j from JobEntity j  where j.consumer = ?1 order by j.created desc")
    List<JobEntity> findLatestJobByConsumer(final @NotNull ConsumerEntity consumer, Pageable pageable);

}
