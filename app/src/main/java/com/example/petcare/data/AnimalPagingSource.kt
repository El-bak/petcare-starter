package com.example.petcare.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.petcare.model.Animal

class AnimalPagingSource(private val animaux: List<Animal> = DonneesAnimaux.animaux) : PagingSource<Int, Animal>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Animal> {
        val page = params.key ?: 0                      // pas de clé = première page
        if (page < 0) {
            return LoadResult.Error(IllegalArgumentException("Clé de page négative : $page"))
        }

        val debut = page * PAGE_SIZE                    // page 0 → 0, page 1 → 10, page 5 → 50
        val fin = minOf(debut + PAGE_SIZE, animaux.size) // ne jamais dépasser la fin de la liste

        val elements = if (debut < animaux.size) animaux.subList(debut, fin).toList() else emptyList()

        return LoadResult.Page(
            data = elements,
            prevKey = if (page == 0) null else page - 1,   // rien avant la première page
            nextKey = if (fin >= animaux.size) null else page + 1   // rien après la dernière
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Animal>): Int? {
        val ancre = state.anchorPosition ?: return null              // position visible à l'écran
        val page = state.closestPageToPosition(ancre) ?: return null // page qui contient cette position
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    companion object {
        // loadSize est une indication : cette source pédagogique charge une page fixe de 10.
        const val PAGE_SIZE = 10
    }
}
