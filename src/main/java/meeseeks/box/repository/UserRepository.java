package meeseeks.box.repository;

import meeseeks.box.domain.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(final String username);
    Optional<UserEntity> findByEmail(final String email);

    @Transactional
    @Modifying
    @Query("delete from UserEntity user where user.username = ?1")
    int deleteUser(final String userName);

    @Query("select user from UserEntity user where user.name like ?1")
    List<UserEntity> findUsersByName(final String name, final Pageable pageable);

    @Query("select user from UserEntity user where user.email like ?1")
    List<UserEntity> findUsersByEmail(final String email, final Pageable pageable);
}
