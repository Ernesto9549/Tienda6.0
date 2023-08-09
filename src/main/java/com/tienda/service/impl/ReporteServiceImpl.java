package com.tienda.service.impl;

import com.tienda.service.ReporteService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    DataSource datasource;

    @Override
    public ResponseEntity<Resource> generaReporte(
            String reporte,
            Map<String, Object> parametros,
            String tipo) throws IOException {

        try {

            //Estilo es si se muestra en pantalla o se descarga;
            String estilo;
            if ("vPdf".equalsIgnoreCase(tipo)) {
                estilo = "inline; ";
            } else {
                estilo = "attachment; ";
            }

            //Se indica la ruta donde están definido los reportes
            String reportePath = "reportes";

            //Se define el "flujo" de salida donde queda el reporte ya generado
            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            //Se construye el acceso al archivo de reporte 
            ClassPathResource fuente = new ClassPathResource(
                    reportePath
                    + File.separator
                    + reporte
                    + ".jasper");

            //Se define un objeto para leer el archivo del reporte .jasper
            InputStream elReporte = fuente.getInputStream();

            //Se crea el reporte "generado" a partir del archivo .jasper + los parámetros + la conexión a la BD
            var reporteJasper = JasperFillManager.fillReport(
                    elReporte,
                    parametros,
                    datasource.getConnection());

            //Esto es para definir el tipo de archivo a responder
            MediaType mediaType = null;
            String archivoSalida = "";

            //Para recuperar la info del reporte generado
            byte[] data;

            if (tipo != null) {
                switch (tipo) {
                    case "Pdf", "vPdf" -> {
                        JasperExportManager
                                .exportReportToPdfStream(
                                        reporteJasper,
                                        salida);
                        mediaType = MediaType.APPLICATION_PDF;
                        archivoSalida = reporte + ".pdf";
                    }
                    ////Falta más... opciones...
                }
            }

            //Se pasa el reporte generado en memoria hacia un arreglo de bytes para enviarlo al usuario...
            data = salida.toByteArray();

            //Se definen los "encabezados" de la página que se responde del reporte...
            HttpHeaders headers = new HttpHeaders();

//Se define el "encabezado" de la página de respuesta            
            headers.set("Content-Disposition", estilo + "filename=\"" + archivoSalida + "\"");

            //Se responde con el documento final
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .contentLength(data.length)
                    .body(new InputStreamResource(new ByteArrayInputStream(data)));

        } catch (SQLException | JRException e) {
            e.printStackTrace();
            return null;
        }

    }

}
