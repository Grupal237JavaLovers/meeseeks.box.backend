package meeseeks.box.repository;

import org.springframework.stereotype.Repository;

import meeseeks.box.domain.ProviderEntity;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface ProviderRepository extends BaseCrudRepository<ProviderEntity, Integer> { }
