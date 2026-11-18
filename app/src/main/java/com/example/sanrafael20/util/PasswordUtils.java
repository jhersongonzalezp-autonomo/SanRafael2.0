package com.example.sanrafael20.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** La base guarda el hash SHA-256 de la contraseña, nunca la contraseña. Solo para fines académicos. */
public final class PasswordUtils {

    private PasswordUtils() { }

    public static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public static boolean coincide(String clave, String hashGuardado) {
        return clave != null && hashGuardado != null && sha256(clave).equalsIgnoreCase(hashGuardado);
    }
}
