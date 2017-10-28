package meeseeks.box.repository;

import meeseeks.box.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    UserEntity findByUsername(final String username);
    Optional<UserEntity> findByEmail(final String email);
}
