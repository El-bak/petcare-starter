package com.example.petcare.data

import com.example.petcare.model.Animal

/** Jeu local immuable fourni pour M2. Ni serveur, ni base, ni Repository. */
object DonneesAnimaux {
    val animaux: List<Animal> = listOf(Animal(1L, "Nala", "cat"), Animal(2L, "Oslo", "dog")) +
        (3L..60L).map { id -> Animal(id, "${listOf("Mina", "Milo", "Luna", "Rio")[(id % 4).toInt()]} $id", if (id % 2L == 0L) "dog" else "cat") }
}
