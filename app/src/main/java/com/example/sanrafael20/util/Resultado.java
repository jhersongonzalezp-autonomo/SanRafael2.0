package com.example.sanrafael20.util;

/** Envuelve la respuesta de una operación: o trae un dato o trae un mensaje de error. */
public final class Resultado<T> {

    private final T dato;
    private final String error;

    private Resultado(T dato, String error) {
        this.dato = dato;
        this.error = error;
    }

    public static <T> Resultado<T> exito(T dato) {
        return new Resultado<>(dato, null);
    }

    public static <T> Resultado<T> error(String mensaje) {
        return new Resultado<>(null, mensaje);
    }

    public boolean esExito() { return error == null; }
    public T getDato() { return dato; }
    public String getError() { return error; }
}
