package com.example.sanrafael20.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** La base guarda las fechas como texto: AAAA-MM-DD y AAAA-MM-DD HH:mm. */
public final class Fechas {

    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Fechas() { }

    public static String hoy() { return LocalDate.now().format(FECHA); }
    public static String ahora() { return LocalDateTime.now().format(FECHA_HORA); }
}
