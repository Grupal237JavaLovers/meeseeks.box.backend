package meeseeks.box.repository;

import meeseeks.box.domain.ReviewEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import meeseeks.box.domain.ProviderEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ProviderRepository extends BaseCrudRepository<ProviderEntity, Integer> {

    @Query("select r from ProviderEntity p join p.reviews r where p = ?1 order by r.rating desc")
    List<ReviewEntity> findTopReviewsForProvider(final @NotNull ProviderEntity provider, Pageable pageable);

}
