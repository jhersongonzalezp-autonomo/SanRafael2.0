package com.example.sanrafael20.data.local.dto;

/**
 * Resultado de una consulta SQL con JOIN: no es una tabla, es un objeto que
 * junta columnas de Consulta y de Paciente para mostrarlas en la agenda.
 * Room llena cada atributo con la columna que tiene el mismo nombre (alias AS).
 */
public class ConsultaConPaciente {
    public long idConsulta;
    public long idPaciente;
    public String fecha;
    public String estado;
    public String motivo;
    public String nombrePaciente;
    public String apellidoPaciente;

    public String getNombreCompleto() { return nombrePaciente + " " + apellidoPaciente; }

    public String getIniciales() {
        String n = nombrePaciente == null || nombrePaciente.isEmpty() ? "" : nombrePaciente.substring(0, 1);
        String a = apellidoPaciente == null || apellidoPaciente.isEmpty() ? "" : apellidoPaciente.substring(0, 1);
        return (n + a).toUpperCase();
    }
}
