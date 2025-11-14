package br.unisanta.applogingoogle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.applogingoogle.databinding.ItemTarefaBinding
import br.unisanta.applogingoogle.model.Tarefas

class TarefasAdapter(private val listaTarefas: List<Tarefas>) :
    RecyclerView.Adapter<TarefasAdapter.TarefaViewHolder>() {

        // A classe de Binding é declarada AQUI como propriedade do ViewHolder
        class TarefaViewHolder(val binding: ItemTarefaBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(tarefa: Tarefas) {
                // Acessando as views de forma segura pelo binding
                binding.txvNome.text = tarefa.nome
                binding.txvCategoria.text = "Categoria: ${tarefa.categoria}"
                binding.txvStatus.text = "Status: ${tarefa.status}"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder {
            val binding = ItemTarefaBinding
                .inflate(LayoutInflater.from(parent.context), parent, false) // <-- Inflação do layout
            return TarefaViewHolder(binding) // <-- Passando para o ViewHolder
        }

        override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
            val tarefa = listaTarefas[position] // Os dados são buscados AQUI
            holder.bind(tarefa) // E passados para o ViewHolder AQUI
        }

        override fun getItemCount(): Int {
            return listaTarefas.size
        }
}