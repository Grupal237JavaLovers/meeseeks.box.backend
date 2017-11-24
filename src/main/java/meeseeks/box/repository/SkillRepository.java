package meeseeks.box.repository;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Repository
public interface SkillRepository extends BaseCrudRepository<SkillEntity, Integer> {

    Set<SkillEntity> findAllByNameContaining(final @NotNull String name, Pageable pageable);

    Optional<SkillEntity> findByName(final @NotNull String name);

    @Query("select skill from SkillEntity skill where ?1 member skill.providers")
    Set<SkillEntity> findAllByProvider(final ProviderEntity provider);

    @Query("select skill from SkillEntity skill where ?#{principal} member skill.providers")
    Set<SkillEntity> getAllSkillsForCurrentProvider();

}
