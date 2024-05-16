package edu.tasklynx.tasklynxjavafx.model;

import java.time.LocalDate;
import java.util.List;

public class Payroll {

    private Trabajador trabajador;
    private List<Trabajo> trabajos;
    private LocalDate fecIni, fecFin;
    private double tiempo;
    private double salario;

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public List<Trabajo> getTrabajos() {
        return trabajos;
    }

    public void setTrabajos(List<Trabajo> trabajos) {
        this.trabajos = trabajos;
    }

    public LocalDate getFecIni() {
        return fecIni;
    }

    public void setFecIni(LocalDate fecIni) {
        this.fecIni = fecIni;
    }

    public LocalDate getFecFin() {
        return fecFin;
    }

    public void setFecFin(LocalDate fecFin) {
        this.fecFin = fecFin;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public Payroll(Trabajador trabajador, List<Trabajo> trabajos, LocalDate fechaInicio, LocalDate fechaFin, double tiempo, double salario) {
        this.trabajador = trabajador;
        this.trabajos = trabajos;
        this.fecIni = fechaInicio;
        this.fecFin = fechaFin;
        this.tiempo = tiempo;
        this.salario = salario;
    }
}
