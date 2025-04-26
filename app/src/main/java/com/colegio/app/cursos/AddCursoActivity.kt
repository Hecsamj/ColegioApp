package com.colegio.app.cursos

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.google.firebase.firestore.FirebaseFirestore

class AddCursoActivity : AppCompatActivity() {

    private lateinit var etNombreCurso: EditText
    private lateinit var etDescripcionCurso: EditText
    private lateinit var spinnerProfesor: Spinner
    private lateinit var btnSeleccionarDias: Button
    private lateinit var tvDiasSeleccionados: TextView
    private lateinit var btnGuardarCurso: Button

    private val db = FirebaseFirestore.getInstance()
    private val dias = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    private val listaHorarios = mutableMapOf<String, Horario>()
    private val diasMarcados = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_curso)

        etNombreCurso = findViewById(R.id.etNombreCurso)
        etDescripcionCurso = findViewById(R.id.etDescripcionCurso)
        spinnerProfesor = findViewById(R.id.spinnerProfesor)
        btnSeleccionarDias = findViewById(R.id.btnSeleccionarDias)
        tvDiasSeleccionados = findViewById(R.id.tvDiasSeleccionados)
        btnGuardarCurso = findViewById(R.id.btnGuardarCurso)

        setupSpinnerProfesor()
        setupSeleccionDias()

        btnGuardarCurso.setOnClickListener {
            guardarCurso()
        }
    }

    private fun setupSpinnerProfesor() {
        val profesores = listOf("Seleccionar Profesor", "Juan Pérez", "Ana Gómez")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, profesores)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProfesor.adapter = adapter
    }

    private fun setupSeleccionDias() {
        val diasChecked = BooleanArray(dias.size)

        btnSeleccionarDias.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Selecciona los días")
                .setMultiChoiceItems(dias, diasChecked) { _, which, isChecked ->
                    val diaSeleccionado = dias[which]
                    if (isChecked && !diasMarcados.contains(diaSeleccionado)) {
                        mostrarDialogoHoras(diaSeleccionado)
                    } else if (!isChecked) {
                        eliminarDia(diaSeleccionado)
                    }
                }
                .setPositiveButton("Aceptar") { _, _ ->
                    actualizarTextViewHorarios()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun mostrarDialogoHoras(dia: String) {
        if (diasMarcados.contains(dia)) return

        // Crear el TimePickerDialog para la hora de inicio
        val timePickerInicio = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val horaInicio = String.format("%02d:%02d", hourOfDay, minute)

                // Crear el TimePickerDialog para la hora de fin
                val timePickerFin = TimePickerDialog(
                    this,
                    { _, hourFin, minuteFin ->
                        val horaFin = String.format("%02d:%02d", hourFin, minuteFin)

                        // Agregar el horario al mapa con la clave del día
                        listaHorarios[dia] = Horario(dia, horaInicio, horaFin)
                        diasMarcados.add(dia)
                        actualizarTextViewHorarios()

                    },
                    0, 0, true
                )
                timePickerFin.show()  // Mostrar el TimePicker de fin

            },
            0, 0, true
        )
        timePickerInicio.show()  // Mostrar el TimePicker de inicio
    }


    private fun eliminarDia(dia: String) {
        listaHorarios.remove(dia)
        diasMarcados.remove(dia)
        actualizarTextViewHorarios()
    }

    private fun actualizarTextViewHorarios() {
        if (listaHorarios.isEmpty()) {
            tvDiasSeleccionados.text = "No hay días seleccionados"
        } else {
            val texto = listaHorarios.entries.joinToString("\n") { entry ->
                "${entry.key}: ${entry.value.horaInicio} - ${entry.value.horaFin}"
            }
            tvDiasSeleccionados.text = texto
        }
    }

    private fun guardarCurso() {
        val nombre = etNombreCurso.text.toString().trim()
        val descripcion = etDescripcionCurso.text.toString().trim()
        val profesorNombre = spinnerProfesor.selectedItem.toString()

        // Asignar un ID de profesor, en este caso asumimos que "Juan Pérez" tiene un ID de "1" y "Ana Gómez" "2"
        val profesorId = when (profesorNombre) {
            "Juan Pérez" -> "1"
            "Ana Gómez" -> "2"
            else -> null
        }

        if (nombre.isEmpty() || descripcion.isEmpty() || profesorNombre == "Seleccionar Profesor" || listaHorarios.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val curso = Curso(
            nombre = nombre,
            descripcion = descripcion,
            profesorId = profesorId,
            profesorNombre = profesorNombre,
            horarios = listaHorarios
        )

        db.collection("cursos")
            .add(curso)
            .addOnSuccessListener {
                Toast.makeText(this, "Curso guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el curso", Toast.LENGTH_SHORT).show()
            }
    }
}
