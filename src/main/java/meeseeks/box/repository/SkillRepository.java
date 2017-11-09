package meeseeks.box.repository;

import meeseeks.box.domain.SkillEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface SkillRepository extends CrudRepository<SkillEntity, Integer> {
    List<SkillEntity> findAllByNameContaining(final @NotNull String name);
    Optional<SkillEntity> findByName(final @NotNull String name);
}
