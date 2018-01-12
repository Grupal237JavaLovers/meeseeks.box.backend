package meeseeks.box.repository;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ReviewRepository extends BaseCrudRepository<ReviewEntity, Integer> {

    @Query("select r from ReviewEntity r where r.provider=?1 and r.receivedByProvider=?2 order by r.date desc")
    List<ReviewEntity> findLatestReviewsProvider(
            final ProviderEntity provider, boolean receivedByProvider, Pageable pageable);

    @Query("select r from ReviewEntity r where r.consumer=?1 and r.receivedByProvider!=?2 order by r.date desc")
    List<ReviewEntity> findLatestReviewsConsumer(
            final ConsumerEntity consumer, boolean receivedByProvider, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update ReviewEntity review set review.rating = ?2, review.message = ?3 " +
            "where review.provider=?#{principal} and review.id = ?1 and review.receivedByProvider = false")
    Integer updateReviewForConsumer(final Integer id, final Integer rating, final String message);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update ReviewEntity review set review.rating = ?2, review.message = ?3 " +
            "where review.consumer=?#{principal} and review.id = ?1 and review.receivedByProvider = true")
    Integer updateReviewForProvider(final Integer id, final Integer rating, final String message);
}
