package com.example.petcare.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/** Simulation locale, sans réseau, heure exacte ni notification différée. */
class PetCareWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // EXERCICE 3 : lire ANIMAL_ID et ANIMAL_NAME, valider, tracer et restituer les données.
        // Échec explicite tant que la vérification n’est pas implémentée.
        return Result.failure()
    }

    companion object {
        const val ANIMAL_ID = "animal_id"
        const val ANIMAL_NAME = "animal_name"
    }
}
