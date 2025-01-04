package co.edu.unicauca.sed.api.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.repository.PeriodoAcademicoRepository;

@Service
public class PeriodoAcademicoService {

    @Autowired
    private PeriodoAcademicoRepository periodoAcademicoRepository;
    private static final Integer ESTADO_ACTIVO = 1;

    /**
     * Recupera todos los períodos académicos disponibles en la base de datos.
     *
     * @return Lista de objetos PeriodoAcademico.
     */
    public Page<PeriodoAcademico> findAll(Pageable pageable) {
        return periodoAcademicoRepository.findAll(pageable);
    }

    /**
     * Busca un período académico por su identificador único (OID).
     *
     * @param oid El identificador del período académico.
     * @return El objeto PeriodoAcademico si se encuentra, o null si no existe.
     */
    public PeriodoAcademico findByOid(Integer oid) {
        Optional<PeriodoAcademico> resultado = this.periodoAcademicoRepository.findById(oid);
        return resultado.orElse(null);
    }

    /**
     * Guarda un nuevo período académico en la base de datos.
     *
     * @param periodoAcademico El objeto PeriodoAcademico que se desea guardar.
     * @return El objeto PeriodoAcademico guardado.
     * @throws IllegalArgumentException Si el ID del período académico ya existe.
     */
    public PeriodoAcademico save(PeriodoAcademico periodoAcademico) {
        // Validar si el ID del período ya existe
        if (periodoAcademicoRepository.existsByIdPeriodo(periodoAcademico.getIdPeriodo())) {
            throw new IllegalArgumentException("El ID del período académico '" + periodoAcademico.getIdPeriodo() + "' ya existe.");
        }

        try {
            // Guardar el período académico
            return periodoAcademicoRepository.save(periodoAcademico);
        } catch (Exception e) {
            // Manejar cualquier error que ocurra durante la operación de guardado
            throw new RuntimeException("Error al guardar el período académico: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un período académico existente en la base de datos.
     *
     * @param oid              El identificador del período académico a actualizar.
     * @param periodoAcademico El objeto PeriodoAcademico con los datos
     *                         actualizados.
     * @return true si la actualización fue exitosa, false si no se encontró el
     *         período académico.
     */
    public boolean update(Integer oid, PeriodoAcademico periodoAcademico) {
        if (periodoAcademicoRepository.existsByIdPeriodo(periodoAcademico.getIdPeriodo())
                && !findByOid(oid).getIdPeriodo().equals(periodoAcademico.getIdPeriodo())) {
            throw new IllegalArgumentException("El ID del período académico ya existe.");
        }
        Optional<PeriodoAcademico> existingPeriodo = periodoAcademicoRepository.findById(oid);
        if (existingPeriodo.isPresent()) {
            periodoAcademico.setOidPeriodoAcademico(oid);
            periodoAcademicoRepository.save(periodoAcademico);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Elimina un período académico de la base de datos por su identificador único
     * (OID).
     *
     * @param oid El identificador del período académico que se desea eliminar.
     */
    public void delete(Integer oid) {
        this.periodoAcademicoRepository.deleteById(oid);
    }

    /**
     * Obtiene el período académico que está marcado como activo en la base de
     * datos.
     *
     * @return Un Optional que contiene el período académico activo si existe.
     */
    private Optional<PeriodoAcademico> getPeriodoAcademicoActivo() {
        return periodoAcademicoRepository.findByEstado(ESTADO_ACTIVO);
    }

    /**
     * Obtiene el identificador del período académico activo.
     *
     * @return El identificador del período académico activo.
     * @throws IllegalStateException Si no se encuentra un período académico activo.
     */
    public Integer obtenerPeriodoAcademicoActivo() {
        return getPeriodoAcademicoActivo()
                .map(PeriodoAcademico::getOidPeriodoAcademico)
                .orElseThrow(() -> new IllegalStateException("No se encontró un período académico activo."));
    }
}
