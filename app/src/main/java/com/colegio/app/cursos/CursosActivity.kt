package com.colegio.app.cursos

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.google.android.material.button.MaterialButton

class CursosActivity : AppCompatActivity() {

    private lateinit var lvCursos: ListView
    private lateinit var btnAddCurso: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cursos)

        lvCursos = findViewById(R.id.lvCursos)
        btnAddCurso = findViewById(R.id.btnAddCurso)

        // Lista de cursos (esto debería venir de la base de datos)
        val cursos = listOf(
            Curso("Matemáticas", "Curso de álgebra y geometría", "Profesor A", "Lunes 10:00 - 12:00"),
            Curso("Historia", "Historia mundial", "Profesor B", "Martes 14:00 - 16:00")
        )

        val cursoAdapter = CursoAdapter(this, cursos)
        lvCursos.adapter = cursoAdapter

        btnAddCurso.setOnClickListener {
            val intent = Intent(this, AddCursoActivity::class.java)
            startActivity(intent)
        }
    }
}
