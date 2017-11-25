package meeseeks.box.repository;

import java.sql.Time;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import meeseeks.box.domain.AvailabilityEntity;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface AvailabilityRepository extends CrudRepository<AvailabilityEntity, Integer>
{
    public Optional<AvailabilityEntity> findByDayAndStartHourAndEndHour(String day, Time startHour, Time endHour);
}
