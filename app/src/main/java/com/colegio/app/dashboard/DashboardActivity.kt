package com.colegio.app.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.colegio.app.auth.LoginActivity
import com.colegio.app.cursos.AddCursoActivity
import com.colegio.app.cursos.Curso
import com.colegio.app.cursos.CursoAdapter
import com.colegio.app.profesores.ProfesoresActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var lvCursos: ListView
    private lateinit var btnAgregarCurso: Button
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var listaCursos: MutableList<Curso> // Lista para almacenar los cursos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        welcomeTextView = findViewById(R.id.tvWelcome)
        logoutButton = findViewById(R.id.btnLogout)
        lvCursos = findViewById(R.id.lvCursos)
        btnAgregarCurso = findViewById(R.id.btnAgregarCurso)

        listaCursos = mutableListOf() // Inicializar la lista de cursos

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Obtener el nombre del usuario desde Firestore
        db.collection("usuarios").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    welcomeTextView.text = "Bienvenido, $nombre"
                } else {
                    welcomeTextView.text = "Bienvenido, ${user.email}"
                }
            }
            .addOnFailureListener {
                welcomeTextView.text = "Bienvenido, ${user.email}"
                Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
            }

        // Obtener los cursos desde Firestore
        obtenerCursos()

        // Ir a AddCursoActivity cuando se haga clic en el botón "Agregar Curso"
        btnAgregarCurso.setOnClickListener {
            val intent = Intent(this, AddCursoActivity::class.java)
            startActivityForResult(intent, 1) // Usamos startActivityForResult para saber cuándo se agrega un curso
        }

        // Botón Profesores
        val btnProfesores = findViewById<MaterialButton>(R.id.btnProfesores)
        btnProfesores.setOnClickListener {
            val intent = Intent(this, ProfesoresActivity::class.java)
            startActivity(intent)
        }

        // Cerrar sesión
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun obtenerCursos() {
        // Obtener los cursos desde Firestore
        db.collection("cursos").get()
            .addOnSuccessListener { result ->
                listaCursos.clear() // Limpiar la lista antes de agregar los nuevos cursos
                for (document in result) {
                    val curso = document.toObject(Curso::class.java)
                    listaCursos.add(curso)
                }
                val adapter = CursoAdapter(this, listaCursos)
                lvCursos.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener los cursos", Toast.LENGTH_SHORT).show()
            }
    }

    // Cuando volvemos de AddCursoActivity, recargamos los cursos
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Se agregó un curso, recargamos la lista
            obtenerCursos()
        }
    }
}
