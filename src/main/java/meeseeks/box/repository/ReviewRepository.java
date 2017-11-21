package meeseeks.box.repository;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
    @Query("select r from ReviewEntity r where r.provider=?1 and r.receivedByProvider=?2 order by r.date desc")
    List<ReviewEntity> findLatestReviewsProvider(final @NotNull ProviderEntity provider, boolean receivedByProvider, Pageable pageable);

    @Query("select r from ReviewEntity r where r.consumer=?1 and r.receivedByProvider!=?2 order by r.date desc")
    List<ReviewEntity> findLatestReviewsConsumer(final @NotNull ConsumerEntity consumer, boolean receivedByProvider, Pageable pageable);
}
