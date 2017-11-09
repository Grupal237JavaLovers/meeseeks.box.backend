package meeseeks.box.repository;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
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
public interface JobRepository extends BaseCrudRepository<JobEntity, Integer> {

    @Transactional
    @Modifying
    @Query("delete from JobEntity job where job.id = ?2 and job.consumer = ?1")
    Long deleteIfCreatedBy(final Integer idConsumer, final Integer idJob);

    @Transactional
    @Modifying
    @Query("update JobEntity job set job = ?3 where job.id = ?2 and job.consumer = ?1")
    void updateIfCreatedBy(final Integer idConsumer, final Integer idJob, final JobEntity job);

    @Query("select j from JobEntity j join j.requests r join r.provider p where p=?1 order by j.created desc")
    List<JobEntity> findLatestJobsRequestedByProvider(final @NotNull ProviderEntity provider, Pageable pageable);

    @Query("select j from JobEntity j  where j.consumer = ?1 order by j.created desc")
    List<JobEntity> findLatestJobsCreatedByConsumer(final @NotNull ConsumerEntity consumer, Pageable pageable);
}
