package com.desaliter.desafio.service;

import com.desaliter.desafio.repository.IConvierteDatos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ConvierteDatos implements IConvierteDatos {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);
        } catch (JsonProcessingException e) {
            System.err.println("Error al convertir JSON: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al procesar JSON", e);
        }
    }
}
