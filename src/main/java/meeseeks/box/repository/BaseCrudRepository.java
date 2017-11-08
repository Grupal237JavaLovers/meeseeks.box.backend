package meeseeks.box.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseCrudRepository<T, K extends Serializable> extends CrudRepository<T, K>
{
    Optional<T> findById(K id);
}
