package br.unisanta.applogingoogle.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.applogingoogle.R
import br.unisanta.applogingoogle.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    // Cliente para interagir com a API de Login do Google
    private lateinit var googleSignInClient: GoogleSignInClient

    // ActivityResultLauncher para lidar com o resultado do login do Google
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                // Sucesso ao pegar a conta Google, agora autenticar no Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GOOGLE_SIGN_IN", "Token do Google recebido, autenticando no Firebase...")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Falha no login com a conta Google
                Log.w("GOOGLE_SIGN_IN", "Login com Google falhou.", e)

                Toast.makeText(this, "Falha no login com Google: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        // Configuração do Cliente de Login do Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // BOTÃO DE LOGIN COM EMAIL E SENHA (CADASTRADO NO AUTHENTICATION - FIREBASE)
        binding.btnLogar.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            // Caso o login for feito por email e senha:
            //  - Verifica se os campos "email" e "senha" foram preenchidos.
            //  - Caso esteja vazio retorna para fazer login novamente.
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha email e senha.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handleSignIn()
                } else {
                    Toast.makeText(
                        this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // BOTÃO DE LOGIN COM O GOOGLE
        binding.btnGoogleLogin.setOnClickListener {
            Log.d("GOOGLE_SIGN_IN", "Botão do Google clicado. Iniciando fluxo de login...")
            signInGoogle() // Chama a função que força o signOut
        }
    }

    // Desloga/limpa a sessão anterior salva do Google.
    // O novo fluxo de login será iniciado SOMENTE APÓS a conclusão da limpeza.
    private fun signInGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            Log.d("GOOGLE_SIGN_OUT", "Sessão Google anterior desconectada. Iniciando novo login.")

            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    // FUNÇÃO PARA O LOGIN COM GOOGLE
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sucesso na autenticação com o Firebase
                handleSignIn()
            } else {
                // Falha na autenticação com o Firebase
                Log.w("FIREBASE_AUTH", "Falha na autenticação com credencial do Google.", task.exception)

                Toast.makeText(
                    this, "Falha na autenticação com o Firebase.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Função executada após o sucesso da autenticação (seja por email/senha ou Google).
    // Exibe uma mensagem de sucesso e inicia a próxima Activity.
    private fun handleSignIn() {
        val user = auth.currentUser
        Toast.makeText(
            this, "Login feito com sucesso! Olá, ${user?.displayName ?: "Usuário"}", Toast.LENGTH_LONG
        ).show()

        val intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
    }
}