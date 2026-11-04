package com.example.petcare.ui

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.data.AnimalPagingSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnimalListActivity : AppCompatActivity() {
    private lateinit var adapter: AnimalAdapter
    private val pages by lazy {
        Pager(PagingConfig(pageSize = AnimalPagingSource.PAGE_SIZE, initialLoadSize = AnimalPagingSource.PAGE_SIZE,
            prefetchDistance = 2, enablePlaceholders = false)) { AnimalPagingSource() }.flow.cachedIn(lifecycleScope)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_list)
        appliquerInsets(findViewById(R.id.root))
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = AnimalAdapter { animal ->
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_ANIMAL_ID, animal.id))
            finish()
        }
        findViewById<RecyclerView>(R.id.animalsRecycler).apply {
            layoutManager = LinearLayoutManager(this@AnimalListActivity)
            adapter = this@AnimalListActivity.adapter
        }
        findViewById<Button>(R.id.retryButton).setOnClickListener { adapter.retry() }
        adapter.addLoadStateListener { etats ->
            val chargement = etats.refresh is LoadState.Loading
            val erreur = etats.refresh is LoadState.Error
            findViewById<View>(R.id.listProgress).visibility = if (chargement) View.VISIBLE else View.GONE
            findViewById<View>(R.id.retryButton).visibility = if (erreur) View.VISIBLE else View.GONE
            findViewById<TextView>(R.id.listStatus).apply {
                visibility = if (!chargement && (erreur || adapter.itemCount == 0)) View.VISIBLE else View.GONE
                setText(if (erreur) R.string.list_error else R.string.list_empty)
            }
        }
        connecterListe()
    }

    private fun connecterListe() {
        // EXERCICE M2.3 : collecter pages pendant STARTED et soumettre au PagingDataAdapter.
        findViewById<TextView>(R.id.listStatus).setText(R.string.list_pending)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }

    companion object { const val EXTRA_ANIMAL_ID = "selected_animal_id" }
}
