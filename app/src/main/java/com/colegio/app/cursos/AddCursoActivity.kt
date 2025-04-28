package com.colegio.app.cursos

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.colegio.app.profesores.Profesor
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

    private val listaProfesores = mutableListOf<Profesor>()
    private lateinit var adapterSpinner: ArrayAdapter<String>

    private var profesorSeleccionadoId: String? = null
    private var profesorSeleccionadoNombre: String? = null

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
        listaProfesores.clear()
        val nombresProfesores = mutableListOf("Seleccionar Profesor")
        adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresProfesores)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProfesor.adapter = adapterSpinner

        // Cargar profesores desde Firestore
        db.collection("profesores")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val nombre = document.getString("nombre") ?: ""
                    val profesor = Profesor(id, nombre)
                    listaProfesores.add(profesor)
                    nombresProfesores.add(nombre)
                }
                adapterSpinner.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar profesores", Toast.LENGTH_SHORT).show()
            }

        spinnerProfesor.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                if (position > 0) {
                    val profesor = listaProfesores[position - 1]  // Restamos 1 porque el primer ítem es "Seleccionar Profesor"
                    profesorSeleccionadoId = profesor.id
                    profesorSeleccionadoNombre = profesor.nombre
                } else {
                    profesorSeleccionadoId = null
                    profesorSeleccionadoNombre = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                profesorSeleccionadoId = null
                profesorSeleccionadoNombre = null
            }
        })
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

        val timePickerInicio = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val horaInicio = String.format("%02d:%02d", hourOfDay, minute)

                val timePickerFin = TimePickerDialog(
                    this,
                    { _, hourFin, minuteFin ->
                        val horaFin = String.format("%02d:%02d", hourFin, minuteFin)

                        listaHorarios[dia] = Horario(dia, horaInicio, horaFin)
                        diasMarcados.add(dia)
                        actualizarTextViewHorarios()
                    },
                    0, 0, true
                )
                timePickerFin.show()

            },
            0, 0, true
        )
        timePickerInicio.show()
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

        if (nombre.isEmpty() || descripcion.isEmpty() || profesorSeleccionadoId == null || listaHorarios.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val curso = Curso(
            nombre = nombre,
            descripcion = descripcion,
            profesorId = profesorSeleccionadoId,
            profesorNombre = profesorSeleccionadoNombre,
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
