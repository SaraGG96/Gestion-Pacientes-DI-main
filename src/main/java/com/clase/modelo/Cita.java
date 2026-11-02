
package com.clase.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private int idcita;
    private String citadnipac;
    private int citaiddoc;
    private LocalDate fechacita;
    private LocalTime horacita;
    private String motivo;
    private String diagnostico;
    private String tratamiento;
    private String estadocita;

    // Constructor vacío
    public Cita() {
    }

    // Constructor con todos los campos
    public Cita(int idcita, String citadnipac, int citaiddoc,
                LocalDate fechacita, LocalTime horacita,
                String motivo, String diagnostico,
                String tratamiento, String estadocita) {

        this.idcita = idcita;
        this.citadnipac = citadnipac;
        this.citaiddoc = citaiddoc;
        this.fechacita = fechacita;
        this.horacita = horacita;
        this.motivo = motivo;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.estadocita = estadocita;
    }

    // Getters y Setters

    public int getIdcita() {
        return idcita;
    }

    public void setIdcita(int idcita) {
        this.idcita = idcita;
    }

    public String getCitadnipac() {
        return citadnipac;
    }

    public String getDniPaciente() {
        return citadnipac;
    }

    public void setCitadnipac(String citadnipac) {
        this.citadnipac = citadnipac;
    }

    public int getCitaiddoc() {
        return citaiddoc;
    }

    public int getIdDoctor() {
        return citaiddoc;
    }

    public void setCitaiddoc(int citaiddoc) {
        this.citaiddoc = citaiddoc;
    }

    public LocalDate getFechacita() {
        return fechacita;
    }

    public LocalDate getFecha() {
        return fechacita;
    }

    public void setFechacita(LocalDate fechacita) {
        this.fechacita = fechacita;
    }

    public LocalTime getHoracita() {
        return horacita;
    }

    public LocalTime getHora() {
        return horacita;
    }

    public void setHoracita(LocalTime horacita) {
        this.horacita = horacita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getEstadocita() {
        return estadocita;
    }

    public String getEstado() {
        return estadocita;
    }

    public void setEstadocita(String estadocita) {
        this.estadocita = estadocita;
    }


}
