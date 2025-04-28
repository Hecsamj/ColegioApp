package com.colegio.app.profesores

data class Profesor(
    var id: String = "",
    var nombre: String = "",
    var especialidad: String = "",
    var correo: String = "",
    var telefono: String = "",
    var fotoUrl: String = "" // Por si luego queremos agregar fotos
)
