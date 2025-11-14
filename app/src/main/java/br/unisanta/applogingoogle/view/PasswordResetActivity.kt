package br.unisanta.applogingoogle.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.applogingoogle.R
import br.unisanta.applogingoogle.databinding.ActivityPasswordResetBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Assumindo que você está usando View Binding para o layout da redefinição
    private lateinit var binding: ActivityPasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicialização do View Binding
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa a instância do Firebase Auth
        auth = Firebase.auth

        // Listener do botão de redefinição
        binding.buttonResetPassword.setOnClickListener {
            // Pega o texto do EditText e remove espaços em branco no início/fim
            val email = binding.editTextEmailReset.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(
                    this, "Por favor, digite seu email de cadastro.", Toast.LENGTH_SHORT
                ).show()
            } else {
                sendPasswordReset(email)
            }
        }
    }

    /**
     * Envia o email de redefinição de senha para o endereço fornecido.
     * @param email O endereço de e-mail do usuário.
     */
    private fun sendPasswordReset(email: String) {
        // Chama a função do Firebase para enviar o email de redefinição
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // SUCESSO: Informa o usuário para checar a caixa de entrada
                    Toast.makeText(
                        this,
                        "Email de redefinição enviado para $email. Verifique sua caixa de entrada.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Fecha a Activity após o envio do e-mail (retorna para a tela de Login)
                    finish()
                } else {
                    // FALHA: Informa o erro, geralmente "Email not found" ou "Invalid email".
                    val errorMessage = task.exception?.message ?: "Erro desconhecido."

                    // É uma boa prática não informar ao usuário se o email existe ou não por questões de segurança.
                    // Mas para depuração, a mensagem completa ajuda.
                    Toast.makeText(
                        this,
                        "Falha ao enviar email. Verifique o endereço e tente novamente.",
                        Toast.LENGTH_LONG
                    ).show()
                    // Você pode logar o erro completo para depuração: Log.e("ResetPassword", errorMessage)
                }
            }
    }
}