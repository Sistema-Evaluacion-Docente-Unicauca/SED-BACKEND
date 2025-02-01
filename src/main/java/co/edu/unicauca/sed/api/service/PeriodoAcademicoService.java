package co.edu.unicauca.sed.api.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.model.EstadoPeriodoAcademico;
import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.repository.EstadoPeriodoAcademicoRepository;
import co.edu.unicauca.sed.api.repository.PeriodoAcademicoRepository;

@Service
public class PeriodoAcademicoService {

    @Autowired
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    @Autowired
    private EstadoPeriodoAcademicoRepository estadoPeriodoAcademicoRepository;

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
        validatePeriodoAcademico(null, periodoAcademico); // Validaciones comunes
        try {
            return periodoAcademicoRepository.save(periodoAcademico);
        } catch (Exception e) {
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
        Integer idEstadoPeriodoAcademico = periodoAcademico.getEstadoPeriodoAcademico().getOidEstadoPeriodoAcademico();
        String errorMessage = "El EstadoPeriodoAcademico con OID " + idEstadoPeriodoAcademico + " no existe.";
        EstadoPeriodoAcademico estado = estadoPeriodoAcademicoRepository.findById(idEstadoPeriodoAcademico).orElseThrow(() -> new IllegalArgumentException(errorMessage));

        // Establecer el estado cargado en el objeto periodoAcademico
        periodoAcademico.setEstadoPeriodoAcademico(estado);

        // Resto de la lógica
        validatePeriodoAcademico(oid, periodoAcademico);

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
     * Valida las condiciones necesarias para guardar o actualizar un período
     * académico.
     *
     * @param oid              El identificador del período académico (puede ser
     *                         null para un nuevo registro).
     * @param periodoAcademico El objeto PeriodoAcademico que se desea guardar o
     *                         actualizar.
     * @throws IllegalArgumentException Si se violan las reglas de validación.
     */
    private void validatePeriodoAcademico(Integer oid, PeriodoAcademico periodoAcademico) {
        // Validar si el ID del período ya existe (para nuevos o actualizaciones con
        // cambio de ID)
        if (periodoAcademicoRepository.existsByIdPeriodo(periodoAcademico.getIdPeriodo()) && (oid == null || !findByOid(oid).getIdPeriodo().equals(periodoAcademico.getIdPeriodo()))) {
            throw new IllegalArgumentException(
                    "El ID del período académico '" + periodoAcademico.getIdPeriodo() + "' ya existe.");
        }

        // Validar si el estado es "ACTIVO"
        EstadoPeriodoAcademico estadoPeriodoAcademico = estadoPeriodoAcademicoRepository.findById(periodoAcademico.getEstadoPeriodoAcademico().getOidEstadoPeriodoAcademico())
                .orElseThrow(() -> new IllegalArgumentException("El EstadoPeriodoAcademico con OID " + periodoAcademico.getEstadoPeriodoAcademico().getOidEstadoPeriodoAcademico() + " no existe."));
        if ("ACTIVO".equals(estadoPeriodoAcademico.getNombre())) {
            Optional<PeriodoAcademico> periodoActivo = getPeriodoAcademicoActivo();
            if (periodoActivo.isPresent()) {
                throw new IllegalArgumentException(
                        "Ya existe un período académico activo con ID: " + periodoActivo.get().getIdPeriodo());
            }
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
        String nombreEstadoActivo = "ACTIVO";
        return periodoAcademicoRepository.findByEstadoPeriodoAcademicoNombre(nombreEstadoActivo);
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
