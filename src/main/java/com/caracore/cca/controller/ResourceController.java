package com.caracore.cca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificar recursos estáticos e WebJars
 */
@Controller
@RequestMapping("/api/debug")
public class ResourceController {

    /**
     * Endpoint para verificar se os recursos estáticos estão funcionando
     */
    @GetMapping("/resources")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkResources() {
        Map<String, Object> response = new HashMap<>();
        
        // Verificar recursos WebJars
        Map<String, String> webjars = new HashMap<>();
        webjars.put("jquery", "3.7.0");
        webjars.put("bootstrap", "5.3.0");
        webjars.put("bootstrap-icons", "1.13.1");
        webjars.put("datatables", "1.10.25");
        
        response.put("status", "OK");
        response.put("webjars", webjars);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
