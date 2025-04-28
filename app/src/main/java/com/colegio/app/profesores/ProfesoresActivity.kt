package com.colegio.app.profesores

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.google.firebase.firestore.FirebaseFirestore

class ProfesoresActivity : AppCompatActivity() {

    private lateinit var listViewProfesores: ListView
    private lateinit var adapter: AdapterProfesor
    private val listaProfesores = mutableListOf<Profesor>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesores)

        listViewProfesores = findViewById(R.id.listViewProfesores)
        adapter = AdapterProfesor(this, listaProfesores)
        listViewProfesores.adapter = adapter

        obtenerProfesores()

        listViewProfesores.setOnItemClickListener { _, _, position, _ ->
            mostrarDialogoOpciones(listaProfesores[position])
        }

        val btnAgregarProfesor = findViewById<Button>(R.id.btnAgregarProfesor)
        btnAgregarProfesor.setOnClickListener {
            mostrarDialogoAgregarEditar(null)
        }
    }

    private fun obtenerProfesores() {
        db.collection("profesores")
            .get()
            .addOnSuccessListener { documentos ->
                listaProfesores.clear()
                for (doc in documentos) {
                    val profesor = doc.toObject(Profesor::class.java)
                    profesor.id = doc.id
                    listaProfesores.add(profesor)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar profesores", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoOpciones(profesor: Profesor) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle(profesor.nombre)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoAgregarEditar(profesor)
                    1 -> eliminarProfesor(profesor)
                }
            }
            .show()
    }

    private fun mostrarDialogoAgregarEditar(profesor: Profesor?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialogo_agregar_profesor, null)
        val etNombre = view.findViewById<EditText>(R.id.etNombreProfesor)
        val etEspecialidad = view.findViewById<EditText>(R.id.etEspecialidadProfesor)
        val etCorreo = view.findViewById<EditText>(R.id.etCorreoProfesor)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefonoProfesor)

        if (profesor != null) {
            etNombre.setText(profesor.nombre)
            etEspecialidad.setText(profesor.especialidad)
            etCorreo.setText(profesor.correo)
            etTelefono.setText(profesor.telefono)
        }

        AlertDialog.Builder(this)
            .setTitle(if (profesor == null) "Agregar Profesor" else "Editar Profesor")
            .setView(view)
            .setPositiveButton(if (profesor == null) "Agregar" else "Actualizar") { _, _ ->
                val nuevoProfesor = Profesor(
                    nombre = etNombre.text.toString(),
                    especialidad = etEspecialidad.text.toString(),
                    correo = etCorreo.text.toString(),
                    telefono = etTelefono.text.toString()
                )
                if (profesor == null) {
                    agregarProfesor(nuevoProfesor)
                } else {
                    actualizarProfesor(profesor.id, nuevoProfesor)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun agregarProfesor(profesor: Profesor) {
        db.collection("profesores")
            .add(profesor)
            .addOnSuccessListener {
                obtenerProfesores()
                Toast.makeText(this, "Profesor agregado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarProfesor(id: String, profesor: Profesor) {
        db.collection("profesores").document(id)
            .set(profesor)
            .addOnSuccessListener {
                obtenerProfesores()
                Toast.makeText(this, "Profesor actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarProfesor(profesor: Profesor) {
        db.collection("profesores").document(profesor.id)
            .delete()
            .addOnSuccessListener {
                obtenerProfesores()
                Toast.makeText(this, "Profesor eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }
}
