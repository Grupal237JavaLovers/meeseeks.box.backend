package meeseeks.box.repository;

import meeseeks.box.domain.AvailabilityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface AvailabilityRepository extends CrudRepository<AvailabilityEntity, Integer> {
    Optional<AvailabilityEntity> findByDayAndStartHourAndEndHour(String day, Time startHour, Time endHour);
}
