package br.unisanta.applogingoogle.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.unisanta.applogingoogle.R
import br.unisanta.applogingoogle.adapter.TarefasAdapter
import br.unisanta.applogingoogle.databinding.ActivityListaTarefasBinding
import br.unisanta.applogingoogle.model.Tarefas
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ListaTarefasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaTarefasBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListaTarefasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvItems.layoutManager = LinearLayoutManager(this)

        buscarTarefasDoFirestore()

        binding.fabVolta.setOnClickListener {
            finish()
        }
    }

    private fun buscarTarefasDoFirestore() {
        val listaTarefas = mutableListOf<Tarefas>()

        db.collection("tarefas")
            .get()
            .addOnSuccessListener { queryResult ->
                for (documento in queryResult.documents) {
                    val tarefa = documento.toObject(Tarefas::class.java)

                    if (tarefa != null) {
                        listaTarefas.add(tarefa)
                    }
                }

                val adapter = TarefasAdapter(listaTarefas)
                binding.rvItems.adapter = adapter

            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Erro ao buscar tarefas: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}