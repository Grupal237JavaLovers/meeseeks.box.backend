package meeseeks.box.repository;

import meeseeks.box.domain.ProviderEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ProviderRepository extends CrudRepository<ProviderEntity, Integer> { }
