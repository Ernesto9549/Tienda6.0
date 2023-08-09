package com.tienda.service;

import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ReporteService {
    
    
    //Este enunciado de método es para emitir un reporte particular en algún formato
    public ResponseEntity<Resource> generaReporte(
            String reporte, //Es el nombre del archivo de .jasper
            Map<String,Object> parametros, //parámetros del reporte
            String tipo //Tipo de archivo generado (pdf, excel, csv)
    ) throws IOException;
}
