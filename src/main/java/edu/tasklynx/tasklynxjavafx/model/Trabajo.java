package edu.tasklynx.tasklynxjavafx.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Trabajo {
    @SerializedName("cod_trabajo")
    private String cod_trabajo;
    private String categoria;
    private String descripcion;
    private Date fec_ini;
    private Date fec_fin;
    private float tiempo;
    private String prioridad;
    private String id_trabajador;

    public Trabajo(String categoria, String descripcion, Date fec_ini, Date fec_fin, float tiempo, String prioridad, String id_trabajador) {
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fec_ini = fec_ini;
        this.fec_fin = fec_fin;
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

    public Date getFec_ini() {
        return fec_ini;
    }

    public void setFec_ini(Date fec_ini) {
        this.fec_ini = fec_ini;
    }

    public Date getFec_fin() {
        return fec_fin;
    }

    public void setFec_fin(Date fec_fin) {
        this.fec_fin = fec_fin;
    }

    public float getTiempo() {
        return tiempo;
    }

    public void setTiempo(float tiempo) {
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

    @Override
    public String toString() {
        return "Trabajo{" +
                "cod_trabajo='" + cod_trabajo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fec_ini=" + fec_ini +
                ", fec_fin=" + fec_fin +
                ", tiempo=" + tiempo +
                ", prioridad='" + prioridad + '\'' +
                ", id_trabajador='" + id_trabajador + '\'' +
                '}';
    }
}
