package com.colegio.app.cursos

data class Curso(
    val id: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val profesorId: String? = null,
    val profesorNombre: String? = null,
    val horarios: Map<String, Horario>? = null
)
