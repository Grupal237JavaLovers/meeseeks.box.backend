package meeseeks.box.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface BaseCrudRepository<T, K extends Serializable> extends CrudRepository<T, K> {
    Optional<T> findById(K id);
}
