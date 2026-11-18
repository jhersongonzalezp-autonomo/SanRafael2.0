package com.example.sanrafael20.data.local.dto;

/** Datos que necesita la factura: la consulta, el paciente, su historia, el médico y el valor. */
public class ConsultaFacturable {
    public long idConsulta;
    public String fechaConsulta;
    public long idPaciente;
    public String nombrePaciente;
    public String apellidoPaciente;
    public String identidad;
    public String numeroHistoria;
    public String nombreMedico;
    public String apellidoMedico;
    public double valorConsulta;

    public String getPaciente() { return nombrePaciente + " " + apellidoPaciente; }
    public String getMedico() { return nombreMedico + " " + apellidoMedico; }
}
