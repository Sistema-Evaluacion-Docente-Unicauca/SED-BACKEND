package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private FileService fileService;

    public Path generarExcelConsolidado(ConsolidadoDTO consolidadoDTO, String nombreDocumento, String nota)
            throws IOException {
        nota = (nota == null) ? "" : nota;
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Consolidado");

            // Llenar datos principales del consolidado
            int currentRow = llenarDatosPrincipales(sheet, consolidadoDTO);

            // Crear encabezados para las actividades
            crearEncabezados(sheet, currentRow++);

            // Llenar actividades en el Excel y calcular sumas
            double totalHS = 0;
            double totalPorcentaje = 0;
            double totalAcumula = 0;

            for (var entry : consolidadoDTO.getActividades().entrySet()) {
                String tipoActividad = entry.getKey();
                List<?> actividades = entry.getValue();

                // Título del tipo de actividad
                Row tipoActividadRow = sheet.createRow(currentRow++);
                Cell cell = tipoActividadRow.createCell(0);
                cell.setCellValue(tipoActividad);
                cell.setCellStyle(crearEstiloTitulo(workbook));

                // Llenar actividades del tipo
                for (Object actividadObj : actividades) {
                    if (actividadObj instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> actividad = (java.util.Map<String, Object>) actividadObj;

                        Row row = sheet.createRow(currentRow++);
                        double hs = (double) ((Float) actividad.get("horas")).floatValue();
                        double porcentaje = (double) ((Float) actividad.get("porcentaje")).floatValue();
                        double acumula = (double) actividad.get("acumulado");

                        row.createCell(0).setCellValue((String) actividad.get("nombre"));
                        row.createCell(1).setCellValue((Float) actividad.get("horas"));
                        row.createCell(2).setCellValue((Float) actividad.get("porcentaje"));
                        row.createCell(5).setCellValue((Double) actividad.get("promedio"));
                        row.createCell(6).setCellValue((Double) actividad.get("acumulado"));

                        // Procesar fuentes
                        List<FuenteDTO> fuentes = (List<FuenteDTO>) actividad.get("fuentes");
                        if (fuentes != null && !fuentes.isEmpty()) {
                            if (fuentes.size() >= 1) {
                                row.createCell(3).setCellValue(fuentes.get(0).getCalificacion());
                            }
                            if (fuentes.size() >= 2) {
                                row.createCell(4).setCellValue(fuentes.get(1).getCalificacion());
                            }
                        }
                        // Sumar totales
                        totalHS += hs;
                        totalPorcentaje += porcentaje;
                        totalAcumula += acumula;
                    }
                }
            }

            // Agregar fila de totales
            Row totalRow = sheet.createRow(currentRow++);
            Cell totalTitleCell = totalRow.createCell(0);
            totalTitleCell.setCellValue("TOTALES:");

            Cell hsCell = totalRow.createCell(1);
            hsCell.setCellValue(totalHS);

            Cell porcentajeCell = totalRow.createCell(2);
            porcentajeCell.setCellValue(totalPorcentaje);

            Cell acumulaCell = totalRow.createCell(6);
            acumulaCell.setCellValue(totalAcumula);

            // Aplicar estilo al total
            CellStyle totalStyle = crearEstiloTitulo(workbook);
            totalTitleCell.setCellStyle(totalStyle);
            hsCell.setCellStyle(totalStyle);
            porcentajeCell.setCellStyle(totalStyle);
            acumulaCell.setCellStyle(totalStyle);

            // Agregar nota después del total
            if (nota != null && !nota.isEmpty()) {
                Row notaRow = sheet.createRow(currentRow++);
                Cell notaCell = notaRow.createCell(0);
                notaCell.setCellValue("Nota: " + nota);

                // Combinar celdas para que la nota abarque toda la tabla
                sheet.addMergedRegion(new CellRangeAddress(
                        notaRow.getRowNum(), // Fila inicial
                        notaRow.getRowNum(), // Fila final
                        0, // Columna inicial
                        6 // Columna final
                ));

                // Estilo para la nota
                CellStyle notaStyle = workbook.createCellStyle();
                Font notaFont = workbook.createFont();
                notaFont.setItalic(true);
                notaStyle.setFont(notaFont);
                notaStyle.setWrapText(true);
                notaCell.setCellStyle(notaStyle);
            }

            // Guardar el archivo usando FileService
            return fileService.saveFile(
                    workbookToMultipartFile(workbook, nombreDocumento),
                    
                    consolidadoDTO.getPeriodoAcademico(),
                    consolidadoDTO.getTipoContratacion(),
                    consolidadoDTO.getDepartamento(),
                    "Consolidados");
        }
    }

    private int llenarDatosPrincipales(Sheet sheet, ConsolidadoDTO consolidadoDTO) {
        int currentRow = 0;

        Row docenteRow = sheet.createRow(currentRow++);
        docenteRow.createCell(0).setCellValue("Nombre del Docente:");
        docenteRow.createCell(1).setCellValue(consolidadoDTO.getNombreDocente());

        Row identificacionRow = sheet.createRow(currentRow++);
        identificacionRow.createCell(0).setCellValue("Número de Identificación:");
        identificacionRow.createCell(1).setCellValue(consolidadoDTO.getNumeroIdentificacion());

        Row periodoRow = sheet.createRow(currentRow++);
        periodoRow.createCell(0).setCellValue("Periodo Académico:");
        periodoRow.createCell(1).setCellValue(consolidadoDTO.getPeriodoAcademico());

        return currentRow;
    }

    private void crearEncabezados(Sheet sheet, int rowNumber) {
        Row headerRow = sheet.createRow(rowNumber);
        headerRow.createCell(0).setCellValue("ACTIVIDAD");
        headerRow.createCell(1).setCellValue("HS");
        headerRow.createCell(2).setCellValue("%");
        headerRow.createCell(3).setCellValue("Fuente 1");
        headerRow.createCell(4).setCellValue("Fuente 2");
        headerRow.createCell(5).setCellValue("Promedio");
        headerRow.createCell(6).setCellValue("Acumula");
    }

    private CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private MultipartFile workbookToMultipartFile(Workbook workbook, String nombreDocumento) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] content = bos.toByteArray();

        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return nombreDocumento + ".xlsx";
            }

            @Override
            public String getContentType() {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }

            @Override
            public boolean isEmpty() {
                return content.length == 0;
            }

            @Override
            public long getSize() {
                return content.length;
            }

            @Override
            public byte[] getBytes() {
                return content;
            }

            @Override
            public java.io.InputStream getInputStream() {
                return new java.io.ByteArrayInputStream(content);
            }

            @Override
            public void transferTo(java.io.File dest) throws IOException {
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
                    fos.write(content);
                }
            }
        };
    }
}
