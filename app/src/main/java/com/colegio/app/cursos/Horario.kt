package com.colegio.app.cursos

data class Horario(
    val dia: String = "",  // Constructor sin argumentos con valores por defecto
    val horaInicio: String = "",
    val horaFin: String = ""
) {
    // Constructor sin argumentos para Firestore
    constructor() : this("", "", "")
}
