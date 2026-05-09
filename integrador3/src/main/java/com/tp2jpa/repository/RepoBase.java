package com.tp2jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface RepoBase<T, ID extends Serializable> extends Repository<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    void deleteById(ID id);
    T save(T entity);
}
