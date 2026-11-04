package com.example.petcare.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/** Simulation locale, sans réseau, heure exacte ni notification différée. */
class PetCareWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val id = inputData.getLong(ANIMAL_ID, -1L)
        val nom = inputData.getString(ANIMAL_NAME)
        if (id <= 0L || nom.isNullOrBlank()) return Result.failure()
        Log.i("PetCareWork", "SIMULATION : vérification de $nom, id=$id")
        return Result.success(workDataOf(ANIMAL_ID to id, ANIMAL_NAME to nom))
    }

    companion object {
        const val ANIMAL_ID = "animal_id"
        const val ANIMAL_NAME = "animal_name"
    }
}
