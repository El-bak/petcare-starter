package com.example.petcare

import com.example.petcare.model.Animal
import com.example.petcare.model.CompteurSoins
import com.example.petcare.work.PetCareWorker
import android.app.Notification
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.petcare.ui.AboutActivity
import com.example.petcare.ui.AnimalListActivity
import com.example.petcare.ui.CareProgressView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.color.MaterialColors
import com.google.android.material.appbar.MaterialToolbar
import com.example.petcare.data.DonneesAnimaux
import android.app.Activity
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    // Acquis M1 : ces fonctions restent corrigées au démarrage de M2.
    private val animaux = DonneesAnimaux.animaux
    private var indexAnimal = 0
    private var animalDuRappel: Animal? = null
    private val animal: Animal get() = animaux[indexAnimal]

    // Démonstration M1 : compteurs en mémoire, sans persistance.
    private val compteurs = CompteurSoins()
    private lateinit var root: View
    private lateinit var animalText: TextView
    private lateinit var countText: TextView
    private lateinit var workText: TextView
    private lateinit var drawer: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var progression: CareProgressView

    // Infrastructure fournie : la liste peut renvoyer l’animal choisi au carnet.
    private val choixAnimal = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultat ->
        if (resultat.resultCode == Activity.RESULT_OK) {
            val id = resultat.data?.getLongExtra(AnimalListActivity.EXTRA_ANIMAL_ID, -1L)
            val index = animaux.indexOfFirst { it.id == id }
            if (index >= 0) { indexAnimal = index; afficherAnimal() }
        }
    }

    private val permissionNotification = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { accordee ->
        if (accordee) afficherNotification() else {
            Toast.makeText(this, R.string.notification_denied, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        root = findViewById(R.id.root)
        animalText = findViewById(R.id.animalText)
        countText = findViewById(R.id.countText)
        workText = findViewById(R.id.workText)
        progression = findViewById(R.id.careProgress)
        restaurerSession(savedInstanceState)
        installerNavigation()
        findViewById<Button>(R.id.animalsButton).setOnClickListener { ouvrirAnimaux() }
        findViewById<Button>(R.id.aboutButton).setOnClickListener { ouvrirPresentation() }
        findViewById<ImageButton>(R.id.animalInfoButton).setOnClickListener {
            val espece = getString(if (animal.especeCode == "cat") R.string.species_cat else R.string.species_dog)
            MaterialAlertDialogBuilder(this).setTitle(animal.nom)
                .setMessage(getString(R.string.animal_information, animal.nom, espece))
                .setPositiveButton(android.R.string.ok, null).show()
        }
        Log.d("PetCareCycle", "onCreate saved=${savedInstanceState != null}")

        // Conserve les marges du XML et évite les barres système.
        // DrawerLayout gère ses enfants sans tenir compte de son propre padding.
        val surface = findViewById<View>(R.id.mainContent)
        val left = surface.paddingLeft
        val top = surface.paddingTop
        val right = surface.paddingRight
        val bottom = surface.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(surface) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(surface)

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            indexAnimal = (indexAnimal + 1) % animaux.size
            afficherAnimal()
        }
        findViewById<Button>(R.id.careButton).setOnClickListener { confirmerAvecToast() }
        findViewById<Button>(R.id.snackButton).setOnClickListener { confirmerAvecAnnulation() }
        findViewById<Button>(R.id.notificationButton).setOnClickListener { demanderNotification() }
        findViewById<Button>(R.id.workButton).setOnClickListener { programmerVerification() }
        creerCanal()
        afficherAnimal()

        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData(WORK_NAME)
            .observe(this) { travaux ->
                workText.text = if (travaux.isNullOrEmpty()) {
                    getString(R.string.work_none)
                } else {
                    travaux.joinToString("\n") { info ->
                        val nom = info.outputData.getString(PetCareWorker.ANIMAL_NAME) ?: "—"
                        getString(R.string.work_state, info.id.toString().take(8), info.state.name, nom)
                    }
                }
            }
    }

    private fun afficherAnimal() {
        val espece = getString(when (animal.especeCode) {
            "cat" -> R.string.species_cat
            "dog" -> R.string.species_dog
            else -> R.string.species_unknown
        })
        animalText.text = getString(R.string.animal_summary, animal.nom, espece)
        val count = compteurs.nombre(animal.id)
        countText.text = resources.getQuantityString(R.plurals.care_count, count, count)
        progression.setProgress(count)
        configurerAccessibilite()
    }

    private fun confirmerAvecToast() {
        compteurs.confirmer(animal.id)
        afficherAnimal()
        Toast.makeText(this, getString(R.string.care_done, animal.nom), Toast.LENGTH_SHORT).show()
    }

    private fun confirmerAvecAnnulation() {
        val animalConcerne = animal
        compteurs.confirmer(animalConcerne.id)
        afficherAnimal()
        Snackbar.make(root, getString(R.string.care_done, animalConcerne.nom), Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                // Annule une seule confirmation du bon animal, même si la sélection change.
                compteurs.annuler(animalConcerne.id)
                afficherAnimal()
            }.show()
    }

    private fun creerCanal() {
        // Kit prévu pour minSdk 26 ou supérieur.
        val canal = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        canal.description = getString(R.string.channel_description)
        getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    private fun demanderNotification() {
        animalDuRappel = animal
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            permissionNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            afficherNotification()
        }
    }

    private fun afficherNotification() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) return

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(this, R.string.notifications_disabled, Toast.LENGTH_LONG).show()
            return
        }
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID)?.importance == NotificationManager.IMPORTANCE_NONE) {
            Toast.makeText(this, R.string.notifications_disabled, Toast.LENGTH_LONG).show()
            return
        }
        val notification = construireRappel(animalDuRappel ?: animal)
        if (notification != null) NotificationManagerCompat.from(this).notify(1001, notification)
    }

    // Acquis M1 : notification immédiate, indépendante du Worker.
    private fun construireRappel(animalConcerne: Animal): Notification? {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pet_notification)
            .setContentTitle(getString(R.string.reminder_title))
            .setContentText(getString(R.string.reminder_text, animalConcerne.nom))
            .setContentIntent(intentionOuvrirApp())
            .setAutoCancel(true)
            .build()
    }

    private fun intentionOuvrirApp(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun programmerVerification() {
        val request = OneTimeWorkRequestBuilder<PetCareWorker>()
            .setInputData(workDataOf(PetCareWorker.ANIMAL_ID to animal.id, PetCareWorker.ANIMAL_NAME to animal.nom))
            .addTag("petcare-demo")
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        Toast.makeText(this, R.string.work_requested, Toast.LENGTH_SHORT).show()
    }


    private fun restaurerSession(etat: Bundle?) {
        // EXERCICE M2.1 : restaurer la sélection et les compteurs depuis etat.
        if (etat == null) return                        // premier lancement : rien à restaurer

        val index = etat.getInt(CLE_INDEX, 0)
        indexAnimal = if (index in animaux.indices) index else 0   // index hors limites → Nala

        val ids = etat.getLongArray(CLE_IDS)
        val valeurs = etat.getIntArray(CLE_VALEURS)
        if (ids != null && valeurs != null && ids.size == valeurs.size) {
            compteurs.restaurer(ids.indices.associate { i -> ids[i] to valeurs[i] })
        }
    }

    private fun enregistrerSession(sortie: Bundle) {
        // EXERCICE M2.1 : enregistrer la sélection et une copie des compteurs dans sortie.
        sortie.putInt(CLE_INDEX, indexAnimal)
        val entrees = compteurs.snapshot().entries.toList()
        sortie.putLongArray(CLE_IDS, entrees.map { it.key }.toLongArray())
        sortie.putIntArray(CLE_VALEURS, entrees.map { it.value }.toIntArray())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        enregistrerSession(outState)
        super.onSaveInstanceState(outState)
    }

    private fun configurerAccessibilite() {
        // EXERCICE M2.2 : titre accessible et description contextualisée du bouton information.
        // Le nom de l'animal est annoncé comme un titre : TalkBack permet de naviguer de titre en titre.
        ViewCompat.setAccessibilityHeading(animalText, true)

        // L'icône ⓘ n'a pas de texte : sa description dit ce qu'elle fait, et pour quel animal.
        findViewById<ImageButton>(R.id.animalInfoButton).contentDescription =
            getString(R.string.animal_info_description, animal.nom)
    }

    private fun installerNavigation() {
        drawer = findViewById(R.id.root)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerToggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        drawerToggle.drawerArrowDrawable.color = MaterialColors.getColor(toolbar, com.google.android.material.R.attr.colorOnPrimary)
        findViewById<NavigationView>(R.id.navigationView).apply {
            setCheckedItem(R.id.nav_home)
            setNavigationItemSelectedListener { item ->
                val traite = traiterDestination(item)
                drawer.closeDrawer(GravityCompat.START)
                traite
            }
        }
        val retour = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() { drawer.closeDrawer(GravityCompat.START) }
        }
        onBackPressedDispatcher.addCallback(this, retour)
        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) { retour.isEnabled = true }
            override fun onDrawerClosed(drawerView: View) { retour.isEnabled = false }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.overflowIcon?.mutate()?.setTint(MaterialColors.getColor(toolbar, com.google.android.material.R.attr.colorOnPrimary))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (drawerToggle.onOptionsItemSelected(item) || traiterActionMenu(item)) true else super.onOptionsItemSelected(item)

    private fun traiterActionMenu(item: MenuItem): Boolean {
        // EXERCICE M2.4 : router chaque identifiant vers la destination prévue.
        Toast.makeText(this, getString(R.string.m2_pending, 4), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun traiterDestination(item: MenuItem): Boolean {
        // EXERCICE M2.4 : router chaque identifiant vers la destination prévue.
        Toast.makeText(this, getString(R.string.m2_pending, 4), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun ouvrirAnimaux() { choixAnimal.launch(Intent(this, AnimalListActivity::class.java)) }
    private fun ouvrirPresentation() { startActivity(Intent(this, AboutActivity::class.java)) }

    override fun onStart() { super.onStart(); Log.d("PetCareCycle", "onStart") }
    override fun onResume() {
        super.onResume()
        findViewById<NavigationView>(R.id.navigationView).setCheckedItem(R.id.nav_home)
        Log.d("PetCareCycle", "onResume")
    }
    override fun onPause() { Log.d("PetCareCycle", "onPause"); super.onPause() }
    override fun onStop() { Log.d("PetCareCycle", "onStop"); super.onStop() }
    override fun onDestroy() { Log.d("PetCareCycle", "onDestroy"); super.onDestroy() }

    companion object {
        private const val CHANNEL_ID = "care_reminders"
        // Une file globale : KEEP ignore une demande si la précédente est inachevée.
        private const val CLE_INDEX = "session_index_animal"
        private const val CLE_IDS = "session_ids"
        private const val CLE_VALEURS = "session_valeurs"
        private const val WORK_NAME = "petcare-check"
    }
}
