package springboot.app.servicios;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import springboot.app.dtos.ReportePerrosHabilidad;
import springboot.app.dtos.PerroDTO;
import springboot.app.modelos.Perro;
import springboot.app.repositorios.PerroRepositorio;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("PerroServicio")
public class PerroServicio implements BaseService<Perro>{

    @Autowired
    private PerroRepositorio perroRepositorio;

    @Transactional
    public List<PerroDTO> buscarPerrosPorHabilidad(String habilidad)throws Exception{

        var resultado = perroRepositorio.getPerrosPorHabilidadOrderByEdadAsc(habilidad);
        try{
            return resultado.stream().map(perro->new PerroDTO(perro.getNombre(),perro.getRaza(),perro.getEdad(),perro.getHabilidad())).collect(Collectors.toList());
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


    @Override
    @Transactional
    public List<Perro> findAll() throws Exception {
        return perroRepositorio.findAll();
    }

    @Override
    @Transactional
    public Perro findById(Long id) throws Exception {
        try{
            Optional<Perro> perroBuscado = perroRepositorio.findById(id);
            return perroBuscado.get();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Perro save(Perro entity) throws Exception {
        try{
            return perroRepositorio.save(entity);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Perro update(Long id, Perro entity) throws Exception {
        try{
            Optional<Perro> entityOpcional = perroRepositorio.findById(id);
            Perro perro = entityOpcional.get();
            perro = perroRepositorio.save(entity);
            return perro;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) throws Exception {
        try{
            if(perroRepositorio.existsById(id)){
                perroRepositorio.deleteById(id);
                return true;
            }else{
                throw new Exception();
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
