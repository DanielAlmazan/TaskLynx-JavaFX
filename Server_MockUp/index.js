const express = require('express');

const app = express();

// Enrutadores
const employees = require(__dirname + '/routes/employees');
const tasks = require(__dirname + '/routes/tasks');

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use('/employees', employees);
app.use('/tasks', tasks);

app.listen(8080);