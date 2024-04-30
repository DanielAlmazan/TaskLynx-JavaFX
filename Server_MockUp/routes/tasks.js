const express = require("express");

const router = express.Router();

const pendingTasks = [
    {
        cod_trabajo: "J001",
        categoria: "Carpintería",
        descripcion: "Construcción de armario",
        fec_ini: '2022-01-01',
        fec_fin: null,
        tiempo: 10.0,
        id_trabajador: 'T001',
        prioridad: 1,
    },
    {
        cod_trabajo: 'J002',
        categoria: 'Fontanería',
        descripcion: 'Reparación de tuberías',
        fec_ini: '2022-01-02',
        fec_fin: null,
        tiempo: 5.0,
        id_trabajador: 'T002',
        prioridad: 2,
    },
    {
        cod_trabajo: 'J003',
        categoria: 'Electricidad',
        descripcion: 'Instalación de luces',
        fec_ini: '2022-01-03',
        fec_fin: null,
        tiempo: 8.0,
        id_trabajador: 'T003',
        prioridad: 3,
    }
];

const completedTasks = [
    {
        cod_trabajo: "J004",
        categoria: "Carpintería",
        descripcion: "Construcción de mesa",
        fec_ini: '2022-01-24',
        fec_fin: '2022-01-26',
        tiempo: 10.0,
        id_trabajador: 'T001',
        prioridad: 1,
    },
    {
        cod_trabajo: "J005",
        categoria: "Fontanería",
        descripcion: "Cambiar grifo",
        fec_ini: '2022-01-24',
        fec_fin: '2022-01-28',
        tiempo: 26.0,
        id_trabajador: 'T002',
        prioridad: 2,
    },
    {
        cod_trabajo: "J006",
        categoria: "Jardineria",
        descripcion: "Regar el jardín",
        fec_ini: '2022-07-12',
        fec_fin: '2022-07-13',
        tiempo: 9.0,
        id_trabajador: 'T003',
        prioridad: 3,
    },
];

router.get("/pending", (req, res) => {
    res.status(200).send({ pendingTasks: pendingTasks });
});

router.get("/completed", (req, res) => {
    res.status(200).send({ completedTasks: completedTasks });
});

module.exports = router;
