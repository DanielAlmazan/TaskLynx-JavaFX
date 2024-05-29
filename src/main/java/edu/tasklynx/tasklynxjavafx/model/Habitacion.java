package edu.tasklynx.tasklynxjavafx.model;

public class Habitacion {
    private String _id;
    private int numero;

    public Habitacion(int numero) {
        this.numero = numero;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "" + numero;
    }
}
