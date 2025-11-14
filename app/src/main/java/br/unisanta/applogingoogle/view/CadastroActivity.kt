package br.unisanta.applogingoogle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.applogingoogle.R
import br.unisanta.applogingoogle.databinding.ActivityCadastroBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Firebase.firestore

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNome.text.toString()
            val categoria = binding.edtCategoria.text.toString()

            val selectedChipId = binding.chipGroupStatus.checkedChipId
            val status = when (selectedChipId) {
                R.id.chip_pendente -> "Pendente"
                R.id.chip_em_andamento -> "Em Andamento"
                R.id.chip_feito -> "Feito"
                else -> "Pendente"
            }

            val tarefa = hashMapOf(
                "nome" to nome,
                "categoria" to categoria,
                "status" to status
            )

            db.collection(
                "tarefas")
                .add(tarefa)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(
                            this,
                            "Tarefa cadastrada com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.edtNome.text.clear()
                        binding.edtCategoria.text.clear()
                        binding.chipGroupStatus.clearCheck()

                    } else {
                        Toast.makeText(
                            this,
                            "Erro ao cadastrar tarefa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        binding.fabLista.setOnClickListener {
            val intent = Intent(this, ListaTarefasActivity::class.java)
            startActivity(intent)
        }
    }
}