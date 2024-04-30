package edu.tasklynx.tasklynxjavafx.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Trabajo {
    @SerializedName("cod_trabajo")
    private String cod_trabajo;
    private String categoria;
    private String descripcion;
    private Date fecIni;
    private Date fecFin;
    private String tiempo;
    private String prioridad;
    private String id_trabajador;

    public Trabajo(String categoria, String descripcion, Date fecIni, Date fecFin, String tiempo, String prioridad, String id_trabajador) {
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = fecIni;
        this.fecFin = fecFin;
        this.tiempo = tiempo;
        this.prioridad = prioridad;
        this.id_trabajador = id_trabajador;
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

    public Date getFecIni() {
        return fecIni;
    }

    public void setFecIni(Date fecIni) {
        this.fecIni = fecIni;
    }

    public Date getFecFin() {
        return fecFin;
    }

    public void setFecFin(Date fecFin) {
        this.fecFin = fecFin;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getId_trabajador() {
        return id_trabajador;
    }

    public void setId_trabajador(String id_trabajador) {
        this.id_trabajador = id_trabajador;
    }
}
