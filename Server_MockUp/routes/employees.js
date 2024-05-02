const express = require("express");

const router = express.Router();

const employees = [
    {
        id_trabajador: 'T001',
        dni: '12345678A',
        nombre: 'Juan',
        apellidos: 'Pérez',
        especialidad: 'Carpintería',
        contraseña: 'contraseña1',
        email: 'juan.perez@example.com'
    },
    {
        id_trabajador: 'T002',
        dni: '23456789B',
        nombre: 'Ana',
        apellidos: 'Gómez',
        especialidad: 'Fontanería',
        contraseña: 'contraseña2',
        email: 'ana.gomez@example.com'
    },
    {
        id_trabajador: 'T003',
        dni: '34567890C',
        nombre: 'Carlos',
        apellidos: 'Martínez',
        especialidad: 'Electricidad',
        contraseña: 'contraseña3',
        email: 'carlos.martinez@example.com'
    },
];

router.get("/", (req, res) => {
    res.status(200).send({ result: employees });
});

module.exports = router;
