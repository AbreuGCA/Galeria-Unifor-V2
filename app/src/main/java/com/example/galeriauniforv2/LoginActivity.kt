package com.example.galeriauniforv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var authManager: UserAuthManager
    var verificacao: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authManager = UserAuthManager()

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        fun verificar(condicao: Boolean) {
            verificacao = condicao
        }



        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authManager.verifyUser(email, password) { success, message ->
                    if (success) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("autenticado", 1)

                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, message ?: "Falha na autenticação", Toast.LENGTH_SHORT).show()
                        // Intention was to pass the authentication status on failure as well
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("autenticado", 0)
                        startActivity(intent)
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
