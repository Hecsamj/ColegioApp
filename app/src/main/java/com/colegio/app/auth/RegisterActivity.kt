package com.colegio.app.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.colegio.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var nameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var spnDocumentType: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var dobEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var selectedDocumentType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.etFirstName)
        lastNameEditText = findViewById(R.id.etLastName)
        spnDocumentType = findViewById(R.id.spinnerDocumentType)
        emailEditText = findViewById(R.id.etEmail)
        phoneEditText = findViewById(R.id.etPhone)
        dobEditText = findViewById(R.id.etDateOfBirth)
        passwordEditText = findViewById(R.id.etPassword)
        registerButton = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        ArrayAdapter.createFromResource(
            this,
            R.array.document_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnDocumentType.adapter = adapter
        }

        spnDocumentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDocumentType = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedDocumentType = "DNI"
            }
        }

        dobEditText.setOnClickListener {
            showDatePickerDialog()
        }

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val dob = dobEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(name, lastName, selectedDocumentType, email, phone, dob, password)
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            dobEditText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun registerUser(
        name: String,
        lastName: String,
        documentType: String,
        email: String,
        phone: String,
        dob: String,
        password: String
    ) {
        progressBar.visibility = View.VISIBLE
        registerButton.isEnabled = false

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            resetUI()
            return
        }

        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
            resetUI()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userMap = hashMapOf(
                        "name" to name,
                        "lastName" to lastName,
                        "documentType" to documentType,
                        "email" to email,
                        "phone" to phone,
                        "dob" to dob,
                        "role" to "Alumno"
                    )

                    firestore.collection("users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            auth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        Log.d("RegisterActivity", "Correo de verificación enviado")
                                        Toast.makeText(this, "Registro exitoso. Verifica tu correo.", Toast.LENGTH_LONG).show()

                                        auth.signOut()
                                        progressBar.visibility = View.GONE

                                        Handler(mainLooper).postDelayed({
                                            val intent = Intent(this, LoginActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            finish()
                                        }, 0)
                                    } else {
                                        Log.e("RegisterActivity", "No se pudo enviar el correo de verificación: ${verificationTask.exception?.message}")
                                        Toast.makeText(this, "No se pudo enviar el correo de verificación.", Toast.LENGTH_SHORT).show()
                                        resetUI()
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                            resetUI()
                        }
                } else {
                    Log.e("RegisterActivity", "Error al registrar usuario: ${task.exception?.message}")
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    resetUI()
                }
            }
    }

    private fun resetUI() {
        progressBar.visibility = View.GONE
        registerButton.isEnabled = true
    }
}
