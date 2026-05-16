package springboot.app.repositorios;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
//import springboot.app.dtos.ReportePerrosHabilidad;
import springboot.app.modelos.Perro;

import java.util.List;

@Repository("PerroRepositorio")
public interface PerroRepositorio extends RepoBase<Perro, Long> {

@Query("SELECT p FROM Perro p WHERE p.habilidad = :habilidad ORDER BY p.edad ASC")
public List<Perro> getPerrosPorHabilidadOrderByEdadAsc(String habilidad);
    List<Perro> findByHabilidad(String habilidad);

}
