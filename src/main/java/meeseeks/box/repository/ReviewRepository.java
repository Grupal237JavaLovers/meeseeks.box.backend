package meeseeks.box.repository;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {

    @Query("select r from ReviewEntity r where r.provider = ?1 order by r.date desc")
    List<ReviewEntity> findReviewsByDateForProvider(final @NotNull ProviderEntity provider, Pageable pageable);

    @Query("select r from ReviewEntity r where r.consumer = ?1 order by r.date desc")
    List<ReviewEntity> findReviewsByDateForConsumer(final @NotNull ConsumerEntity consumer, Pageable pageable);

    @Query("select r from ReviewEntity r where r.provider = ?1 order by r.rating desc")
    List<ReviewEntity> findReviewsByRatingForProvider(final @NotNull ProviderEntity provider, Pageable pageable);

    @Query("select r from ReviewEntity r where r.consumer = ?1 order by r.rating desc")
    List<ReviewEntity> findReviewsByRatingForConsumer(final @NotNull ConsumerEntity consumer, Pageable pageable);

}
