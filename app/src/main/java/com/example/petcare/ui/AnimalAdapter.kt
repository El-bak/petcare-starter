package com.example.petcare.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.Animal

class AnimalAdapter(private val choisir: (Animal) -> Unit) : PagingDataAdapter<Animal, AnimalAdapter.AnimalHolder>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalHolder =
        AnimalHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_animal, parent, false), choisir)

    override fun onBindViewHolder(holder: AnimalHolder, position: Int) {
        val animal = getItem(position)
        if (animal != null) {
            holder.afficher(animal)
        } else {
            // Élément pas encore chargé : ligne neutre, non cliquable.
            holder.itemView.findViewById<TextView>(R.id.nameText).setText(R.string.row_pending)
            holder.itemView.findViewById<TextView>(R.id.speciesText).text = ""
            holder.itemView.setOnClickListener(null)
        }
    }

    class AnimalHolder(view: View, private val choisir: (Animal) -> Unit) : RecyclerView.ViewHolder(view) {
        fun afficher(animal: Animal) {
            itemView.findViewById<TextView>(R.id.nameText).text = animal.nom
            itemView.findViewById<TextView>(R.id.speciesText).setText(
                when (animal.especeCode) { "cat" -> R.string.species_cat; "dog" -> R.string.species_dog; else -> R.string.species_unknown }
            )
            ViewCompat.setScreenReaderFocusable(itemView, true)
            itemView.setOnClickListener { choisir(animal) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Animal>() {
            override fun areItemsTheSame(oldItem: Animal, newItem: Animal) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Animal, newItem: Animal) = oldItem == newItem
        }
    }
}
