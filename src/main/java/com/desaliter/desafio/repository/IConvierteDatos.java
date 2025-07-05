package com.desaliter.desafio.repository;

public interface IConvierteDatos {
    <T> T obenerDatos(String json, Class<T> clase);
}
