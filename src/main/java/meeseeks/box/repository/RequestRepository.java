package meeseeks.box.repository;

import meeseeks.box.domain.RequestEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface RequestRepository extends BaseCrudRepository<RequestEntity, Integer> {

    @Query("select request from RequestEntity request where request.id_job = ?1 order by request.date desc")
    List<RequestEntity> findLatestRequestsFromJob(final Integer idJob, final Pageable pageable);

    @Query("select request from RequestEntity request where request.id_provider = ?1 order by request.date desc")
    List<RequestEntity> findLatestRequestsForProvider(final Integer idProvider, final Pageable pageable);

    @Query("select request from RequestEntity request where request.date between ?1 and ?2 order by request.date desc")
    List<RequestEntity> findByDateBetween(final Calendar start, final Calendar end, final Pageable pageable);

    @Query("select request from RequestEntity request where request.id_provider = ?1 and request.accepted = true order by request.date desc")
    List<RequestEntity> findLatestAcceptedRequestsForProvider(final Integer idProvider, final Pageable pageable);
}
