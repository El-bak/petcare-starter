package com.example.petcare.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.petcare.model.Animal

class AnimalPagingSource(private val animaux: List<Animal> = DonneesAnimaux.animaux) : PagingSource<Int, Animal>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Animal> {
        // EXERCICE M2.3 : page fixe de PAGE_SIZE éléments, clés précédente/suivante.
        // Une page vide permet de démarrer sans erreur avant l’exercice.
        return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
    }

    override fun getRefreshKey(state: PagingState<Int, Animal>): Int? {
        // EXERCICE M2.3 : retrouver une clé à partir de l’ancre et de sa page voisine.
        return null
    }

    companion object {
        // loadSize est une indication : cette source pédagogique charge une page fixe de 10.
        const val PAGE_SIZE = 10
    }
}
