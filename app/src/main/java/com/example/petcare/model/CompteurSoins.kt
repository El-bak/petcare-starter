package com.example.petcare.model

/** Support fourni en M1. Mémoire de session uniquement, aucune base de données. */
class CompteurSoins {
    private val comptes = mutableMapOf<Long, Int>()
    fun nombre(id: Long): Int = comptes[id] ?: 0
    fun confirmer(id: Long) { comptes[id] = nombre(id) + 1 }
    fun annuler(id: Long) { comptes[id] = (nombre(id) - 1).coerceAtLeast(0) }
}
