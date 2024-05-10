package edu.tasklynx.tasklynxjavafx.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class Trabajo {
    @SerializedName("codTrabajo")
    private String codTrabajo;
    private String categoria;
    private String descripcion;
    private LocalDate fecIni;
    private LocalDate fecFin;
    private Float tiempo;
    private int prioridad;
    private Trabajador idTrabajador;
    private String nombreTrabajador;

    public Trabajo(String codTrabajo, String categoria, String descripcion, LocalDate fecIni, LocalDate fecFin, Float tiempo, Integer prioridad, Trabajador idTrabajador) {
        this.codTrabajo = codTrabajo;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = fecIni;
        this.fecFin = fecFin;
        this.tiempo = tiempo;
        this.prioridad = prioridad;
        this.idTrabajador = idTrabajador;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecIni() {
        return fecIni;
    }

    public void setFecIni(LocalDate fec_ini) {
        this.fecIni = fec_ini;
    }

    public LocalDate getFecFin() {
        return fecFin;
    }

    public void setFecFin(LocalDate fec_fin) {
        this.fecFin = fec_fin;
    }

    public float getTiempo() {
        return tiempo;
    }

    public void setTiempo(float tiempo) {
        this.tiempo = tiempo;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public Trabajador getIdTrabajador() {
        return idTrabajador;
    }

    public void setId_trabajador(Trabajador id_trabajador) {
        this.idTrabajador = id_trabajador;
    }

    public String getNombreTrabajador() {
        return idTrabajador.toString();
    }

    @Override
    public String toString() {
        return "Trabajo{" +
                "cod_trabajo='" + codTrabajo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fec_ini=" + fecIni +
                ", fec_fin=" + fecFin +
                ", tiempo=" + tiempo +
                ", prioridad='" + prioridad + '\'' +
                ", id_trabajador='" + idTrabajador + '\'' +
                '}';
    }
}
