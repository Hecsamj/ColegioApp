package com.colegio.app.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Verificar si el usuario está autenticado
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Si no hay usuario autenticado, redirigir al LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Termina esta actividad para que no se pueda regresar
            return
        }

        // Referencia a los elementos de la UI
        welcomeTextView = findViewById(R.id.tvWelcome)
        logoutButton = findViewById(R.id.btnLogout)

        // Mostrar el correo del usuario autenticado
        welcomeTextView.text = "Bienvenido, ${user.email}"

        // Configurar el botón de cerrar sesión
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()  // Cierra la sesión de Firebase
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

            // Redirige al LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Finaliza la actividad actual
        }
    }
}
