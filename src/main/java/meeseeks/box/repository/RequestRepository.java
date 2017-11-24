package meeseeks.box.repository;

import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.RequestEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface RequestRepository extends BaseCrudRepository<RequestEntity, Integer> {

    @Query("select request from RequestEntity request where request.job = ?1 order by request.date desc")
    List<RequestEntity> findLatestRequestsFromJob(final JobEntity job, final Pageable pageable);

    @Query("select request from RequestEntity request where request.provider = ?1 order by request.date desc")
    List<RequestEntity> findLatestRequestsForProvider(final ProviderEntity provider, final Pageable pageable);

    @Query("select request from RequestEntity request where request.date between ?1 and ?2 order by request.date desc")
    List<RequestEntity> findByDateBetween(final Calendar start, final Calendar end, final Pageable pageable);

    @Query("select request from RequestEntity request where request.provider = ?1 and request.accepted = true order by request.date desc")
    List<RequestEntity> findLatestAcceptedRequestsForProvider(final ProviderEntity provider, final Pageable pageable);

    @Query("select request from RequestEntity request where request.provider = ?1 and request.job = ?2")
    Optional<RequestEntity> getRequestByProviderAndJob(final ProviderEntity provider, final JobEntity job);

    @Query("select request from RequestEntity request where request.provider = ?#{principal}")
    List<RequestEntity> getAllRequestsForCurrentProvider();

    @Query("select request from RequestEntity request where request.provider = ?#{principal}")
    List<RequestEntity> getRequestsForCurrentProvider(final Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from RequestEntity request where request.provider = ?#{principal} and request.id = ?1")
    Integer deleteRequestFromCurrentProvider(final Integer id);

    @Modifying
    @Transactional
    @Query("update RequestEntity request set request.message = ?2 where request.provider = ?#{principal} and request.id = ?1")
    Integer updateRequestFromCurrentProvider(final Integer id, final String message);

}
