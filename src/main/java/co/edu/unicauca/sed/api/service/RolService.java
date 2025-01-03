package co.edu.unicauca.sed.api.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para la gestión de roles.
 * Proporciona métodos para realizar operaciones CRUD sobre los roles.
 */
@Service
public class RolService {

    private static final Logger logger = LoggerFactory.getLogger(RolService.class);

    @Autowired
    private RolRepository rolRepository;

    /**
     * Recupera todos los roles disponibles.
     *
     * @return Lista de roles.
     */
    public Page<Rol> findAll(Pageable pageable) {
        return rolRepository.findAll(pageable);
    }

    /**
     * Busca un rol por su ID.
     *
     * @param oid El ID del rol.
     * @return El rol encontrado o null si no existe.
     */
    public Rol findByOid(Integer oid) {
        Optional<Rol> resultado = rolRepository.findById(oid);
        return resultado.orElse(null);
    }

    /**
     * Guarda un nuevo rol.
     *
     * @param rol El objeto Rol a guardar.
     * @return El rol guardado.
     */
    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    /**
     * Actualiza un rol existente.
     *
     * @param oid El ID del rol a actualizar.
     * @param rol Datos actualizados del rol.
     * @return true si la actualización fue exitosa, false si el rol no existe.
     */
    public boolean update(Integer oid, Rol rol) {
        Optional<Rol> existingRol = rolRepository.findById(oid);
        if (existingRol.isPresent()) {
            rol.setOid(oid);
            rolRepository.save(rol);
            return true;
        } else {
            logger.warn("Rol con ID {} no encontrado.", oid);
            return false;
        }
    }

    /**
     * Elimina un rol por su ID.
     *
     * @param oid El ID del rol a eliminar.
     */
    public void delete(Integer oid) {
        logger.info("Eliminando rol con ID: {}", oid);
        rolRepository.deleteById(oid);
    }
}
