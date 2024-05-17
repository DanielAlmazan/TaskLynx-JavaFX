package edu.tasklynx.tasklynxjavafx.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Trabajador {

    @SerializedName("idTrabajador")
    private String idTrabajador;
    private String dni;
    private String nombre;
    private String apellidos;
    private String contraseña;
    private String email;

    private String especialidad;

    public Trabajador(String idTrabajador, String dni, String nombre, String apellidos, String contraseña, String email, String especialidad) {
        this.idTrabajador = idTrabajador;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.contraseña = contraseña;
        this.email = email;
        this.especialidad = especialidad;
    }
    
    public Trabajador() { }

    public String getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(String idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trabajador that = (Trabajador) o;
        return Objects.equals(idTrabajador, that.idTrabajador);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idTrabajador);
    }

    @Override
    public String toString() {
        return idTrabajador == null ? "" : idTrabajador + " - " + nombre + " " + apellidos;
    }
}
