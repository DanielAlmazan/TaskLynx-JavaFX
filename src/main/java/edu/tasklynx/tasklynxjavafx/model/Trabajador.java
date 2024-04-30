package edu.tasklynx.tasklynxjavafx.model;

import com.google.gson.annotations.SerializedName;

public class Trabajador {
    @SerializedName("id_trabajador")
    private String id_trabajador;
    private String dni;
    private String nombre;
    private String apellidos;
    private String contraseña;
    private String email;

    public Trabajador(String dni, String nombre, String apellidos, String contraseña, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.contraseña = contraseña;
        this.email = email;
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
}
