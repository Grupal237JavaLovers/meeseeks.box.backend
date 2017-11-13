package meeseeks.box.repository;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ConsumerRepository extends BaseCrudRepository<ConsumerEntity, Integer> {

    @Query("select r from ConsumerEntity c join c.reviews r where c = ?1 order by r.rating desc")
    List<ReviewEntity> findTopReviewsForConsumer(final @NotNull ConsumerEntity consumer, Pageable pageable);

}
