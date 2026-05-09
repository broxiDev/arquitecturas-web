package com.tp2jpa.repository;

import com.tp2jpa.entities.EstudianteCarrera;
import org.springframework.stereotype.Repository;

@Repository("EstudianteCarreraRepository")
public interface EstudianteCarreraRepository extends RepoBase<EstudianteCarrera, Long> {
}
