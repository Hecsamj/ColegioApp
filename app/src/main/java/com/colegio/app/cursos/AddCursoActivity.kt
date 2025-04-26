package com.colegio.app.cursos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.google.firebase.firestore.FirebaseFirestore
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class AddCursoActivity : AppCompatActivity() {

    private lateinit var etCursoNombre: EditText
    private lateinit var etCursoDescripcion: EditText
    private lateinit var etCursoProfesor: EditText
    private lateinit var etCursoHorario: EditText
    private lateinit var btnGuardarCurso: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_curso)

        etCursoNombre = findViewById(R.id.etCursoNombre)
        etCursoDescripcion = findViewById(R.id.etCursoDescripcion)
        etCursoProfesor = findViewById(R.id.etCursoProfesor)
        etCursoHorario = findViewById(R.id.etCursoHorario)
        btnGuardarCurso = findViewById(R.id.btnGuardarCurso)

        // Configurar el selector de horario
        etCursoHorario.setOnClickListener {
            val now = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog.newInstance(
                { _, hourOfDay, minute, _ ->
                    val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                    etCursoHorario.setText(formattedTime)
                },
                now[Calendar.HOUR_OF_DAY], now[Calendar.MINUTE], true
            )
            timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
        }

        btnGuardarCurso.setOnClickListener {
            guardarCurso()
        }
    }

    private fun guardarCurso() {
        val nombre = etCursoNombre.text.toString().trim()
        val descripcion = etCursoDescripcion.text.toString().trim()
        val profesor = etCursoProfesor.text.toString().trim()
        val horario = etCursoHorario.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || profesor.isEmpty() || horario.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoCurso = Curso(
            nombre = nombre,
            descripcion = descripcion,
            profesor = profesor,
            horario = horario
        )

        db.collection("cursos")
            .add(nuevoCurso)
            .addOnSuccessListener {
                Toast.makeText(this, "Curso guardado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CursosActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el curso", Toast.LENGTH_SHORT).show()
            }
    }
}
