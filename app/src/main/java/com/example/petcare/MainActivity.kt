package com.example.petcare

import com.example.petcare.model.Animal
import com.example.petcare.model.CompteurSoins
import com.example.petcare.work.PetCareWorker
import android.app.Notification
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
    // EXERCICE 1 : données de démonstration, pas de saisie ni de persistance en M1.
    private val animaux = listOf(
        Animal(1L, "Alex", "cat"),
        Animal(2L, "Marcel", "dog")
    )
    private var indexAnimal = 0
    private var animalDuRappel: Animal? = null
    private val animal: Animal get() = animaux[indexAnimal]

    // Démonstration M1 : compteurs en mémoire, sans persistance.
    private val compteurs = CompteurSoins()
    private lateinit var root: View
    private lateinit var animalText: TextView
    private lateinit var countText: TextView
    private lateinit var workText: TextView

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

        // Conserve les marges du XML et évite les barres système.
        val left = root.paddingLeft
        val top = root.paddingTop
        val right = root.paddingRight
        val bottom = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(root)

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
    }

    private fun confirmerAvecToast() {
        // EXERCICE 2A : confirmer pour animal.id, rafraîchir, afficher un Toast avec care_done.
        exerciceACompleter(2)
    }

    private fun confirmerAvecAnnulation() {
        // EXERCICE 2B : capturer animal, confirmer et proposer Annuler dans un Snackbar.
        // CompteurSoins.confirmer/annuler et afficherAnimal sont déjà fournis.
        exerciceACompleter(2)
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

    // EXERCICE 2 : contenu du rappel. Canal, permission et PendingIntent sont fournis.
    private fun construireRappel(animalConcerne: Animal): Notification? {
        // EXERCICE 2C : retourner une notification construite avec les ressources du rappel.
        // Utiliser intentionOuvrirApp() ; canal et permission sont déjà gérés.
        exerciceACompleter(2)
        return null // Aucun rappel tant que cette partie reste à compléter.
    }

    private fun intentionOuvrirApp(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun programmerVerification() {
        // EXERCICE 3 : créer une OneTimeWorkRequest<PetCareWorker> avec les deux entrées,
        // puis l’envoyer avec WORK_NAME et ExistingWorkPolicy.KEEP.
        exerciceACompleter(3)
    }

    private fun exerciceACompleter(numero: Int) {
        Toast.makeText(this, getString(R.string.exercise_pending, numero), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CHANNEL_ID = "care_reminders"
        // Une file globale : KEEP ignore une demande si la précédente est inachevée.
        private const val WORK_NAME = "petcare-check"
    }
}
