# PetCare — starter étudiant M1

PetCare est une application pédagogique de carnet de soins pour animaux. Vous partirez de ce projet pour apprendre Android en Kotlin avec **Views/XML**. Le projet compile et s’ouvre avant les exercices. Les boutons à compléter affichent un message « Exercice … : fonction à compléter » ; ce n’est pas une erreur de configuration.

## 1. Forker, cloner et ouvrir

1. Ouvrez le dépôt **étudiant** indiqué par le formateur sur GitHub, puis cliquez sur **Fork**. Conservez le fork dans votre compte.
2. Sur la page de votre fork, cliquez sur **Code** et copiez son URL HTTPS ou SSH. Clonez **votre fork**, pas le dépôt du formateur :

```powershell
git clone URL_COPIEE_DE_VOTRE_FORK petcare-starter
cd petcare-starter
git remote -v
```

`URL_COPIEE_DE_VOTRE_FORK` est à remplacer par l’URL réelle de votre compte. `origin` doit pointer vers votre fork. HTTPS peut demander une authentification GitHub ; SSH exige une clé déjà configurée. Aucun corrigé n’est nécessaire.

3. Dans Android Studio : **Open**, sélectionnez le dossier `petcare-starter` contenant `settings.gradle.kts`. Ne créez pas de nouveau projet et n’ouvrez pas uniquement le dossier `app`.
4. Laissez la synchronisation Gradle et les téléchargements finir. Une connexion Internet est nécessaire au premier lancement. Ne migrez pas les versions proposées par l’IDE pendant le cours.

## 2. Configuration vérifiable

| Élément | Version du projet |
| --- | --- |
| Android Studio | Otter 2 (2025.2.2) ou version compatible avec AGP 8.13.2 |
| Gradle JDK | 17 ou 21, configuré dans Settings > Build, Execution, Deployment > Build Tools > Gradle |
| Gradle Wrapper / AGP / Kotlin | 8.13 / 8.13.2 / 2.3.0 |
| Android SDK Platform | API 36, à installer via SDK Manager |
| SDK Build Tools | 35.0.0 |
| Android minimum / cible | API 26 / API 35 |
| Émulateur conseillé | API 35, image correspondant au processeur de votre machine |

Dans **Device Manager**, créez ou démarrez un appareil virtuel. Sélectionnez `app` et cet appareil dans la barre d’outils, puis **Run**. Un appareil Android physique API 26+ avec débogage USB est aussi possible. API 33+ permet de pratiquer la demande de permission des notifications. Vérifiez que `adb devices` indique `device` si le formateur vous demande un diagnostic ADB ; `adb` se trouve dans `platform-tools` de votre SDK.

Android Studio écrit le chemin du SDK dans `local.properties`, propre à votre machine et ignoré par Git. En terminal, vous pouvez aussi configurer `ANDROID_HOME`. `JAVA_HOME` doit viser le JDK choisi, jamais Java 8. Aucun chemin absolu du formateur n’est requis.

À la racine du projet, sous PowerShell :

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

Sous macOS/Linux : `chmod +x gradlew`, puis `./gradlew assembleDebug testDebugUnitTest lintDebug`. Utilisez le Wrapper fourni, sans installer Gradle globalement. L’APK apparaît dans `app/build/outputs/apk/debug/app-debug.apk`. Compiler ne lance pas l’application : utilisez Run pour l’installer et la démarrer.

## 3. Ce qui est fourni

L’écran, les cinq boutons, le modèle `Animal`, les compteurs de session, les imports, les dépendances, le canal de notification, la permission API 33+, le PendingIntent et l’observation WorkManager sont présents. Vous ne devez pas réparer leur configuration.

Dans `app/src/main/java/com/example/petcare/` :

| Fichier | Contenu |
| --- | --- |
| `MainActivity.kt` | `onCreate`, `afficherAnimal`, boutons et fonctions des exercices |
| `model/Animal.kt` | `data class Animal(id, nom, especeCode)` |
| `model/CompteurSoins.kt` | `confirmer(id)`, `annuler(id)`, `nombre(id)`, déjà opérationnels |
| `work/PetCareWorker.kt` | classe Worker, constantes `ANIMAL_ID` et `ANIMAL_NAME` |

L’écran se trouve dans `app/src/main/res/layout/activity_main.xml`. Les textes par défaut sont dans `app/src/main/res/values/strings.xml`, l’anglais dans `app/src/main/res/values-en/strings.xml`. Le manifeste est `app/src/main/AndroidManifest.xml`. Aucun fichier de layout supplémentaire n’est nécessaire en M1.

## Exercice 1 — construire et lancer votre fork (environ 20 min)

**Objectif :** comprendre le point de départ et déployer votre application.

1. Réalisez le fork, le clone, la synchronisation et le premier lancement décrits ci-dessus.
2. Lisez `Animal.kt`. Dans la liste `animaux` de `MainActivity.kt`, personnalisez les noms de Nala et Oslo. Conservez deux IDs positifs distincts, et les codes `cat` et `dog` : les libellés affichés viennent des ressources traduites.
3. Repérez `onCreate`, `setContentView(R.layout.activity_main)` et `afficherAnimal()`. Retrouvez `animalText`, `countText` et `nextButton` dans le XML, ainsi que l’Activity de lancement dans le manifeste.
4. Reconstruisez et relancez l’app ; utilisez « Changer d’animal ».

**Réussite :** votre fork compile, démarre, montre les noms personnalisés et permet d’alterner les deux animaux. Expliquez la différence entre code Kotlin, ressources, manifeste et fichiers Gradle. Pour comprendre comment un projet serait créé, le formateur montre *Empty Views Activity* ; vous continuez à utiliser ce dépôt.

## Exercice 2 — interactions et notification immédiate (environ 25 min)

**Fichier :** `MainActivity.kt`. Complétez les trois fonctions signalées `EXERCICE 2`, en remplaçant les appels au message d’attente. Les boutons appellent déjà ces fonctions.

### A. `confirmerAvecToast()` — bouton `careButton`

Enregistrez un soin pour l’animal sélectionné avec `compteurs.confirmer(animal.id)`, rafraîchissez via `afficherAnimal()`, puis affichez un Toast court contenant le texte `care_done` et le nom de l’animal. La ressource attend un argument `%1$s` : utilisez `getString` avec ce nom. Pensez à afficher le Toast, pas seulement à le créer.

### B. `confirmerAvecAnnulation()` — bouton `snackButton`

Capturez l’animal concerné dans une variable locale, confirmez son soin et rafraîchissez l’affichage. Affichez un Snackbar attaché à `root`, avec `care_done` et une action `undo`. L’action doit annuler **un** soin du même animal grâce à `compteurs.annuler(id)`, puis rafraîchir. La sélection peut changer pendant que le Snackbar est visible : utilisez la variable capturée pour l’annulation.

### C. `construireRappel(animalConcerne)` — bouton `notificationButton`

Remplacez `return null` par une notification construite avec `NotificationCompat.Builder(this, CHANNEL_ID)`. Utilisez :

- `R.drawable.ic_pet_notification` comme petite icône ;
- `reminder_title` comme titre ;
- `reminder_text` avec `animalConcerne.nom` comme texte ;
- `intentionOuvrirApp()` comme intention au toucher ;
- l’option de suppression automatique après toucher, puis `build()`.

Le canal et les contrôles de permission sont fournis par `creerCanal`, `demanderNotification` et `afficherNotification`. L’affichage est immédiat après le bouton ; ce n’est pas une alarme différée. Un nouveau rappel remplace le précédent.

**Réussite :** Toast visible et compteur +1 ; Snackbar +1 puis Annuler -1 ; l’annulation reste correcte après changement d’animal. Sur API 33+, accordez la permission : le rappel porte le nom attendu et ouvre l’app au toucher. Si vous refusez, l’application reste utilisable. Pour réessayer après un refus ou un canal bloqué, autorisez les notifications dans les réglages de PetCare. Le son et la bannière dépendent des réglages Android.

## Exercice 3 — traduire et planifier une vérification locale (environ 25 min)

### A. Ressources anglaises

Dans `app/src/main/res/values-en/strings.xml`, ajoutez les quatre traductions manquantes : `care_done`, `undo`, `reminder_title`, `reminder_text`. Leur sens et leurs paramètres sont dans `values/strings.xml`. Conservez `%1$s` pour le nom. Ne changez pas les identifiants des ressources ou les codes `cat`/`dog`.

Le starter reste utilisable avant cette étape : les textes absents retombent sur les ressources françaises. Les quatre annotations Lint `tools:ignore="MissingTranslation"` dans les valeurs par défaut documentent uniquement cette omission volontaire ; vous pouvez les retirer après traduction. Aucun contrôle global Lint n’est désactivé.

### B. `MainActivity.programmerVerification()`

Construisez une demande ponctuelle `OneTimeWorkRequestBuilder<PetCareWorker>`. Donnez-lui les entrées `PetCareWorker.ANIMAL_ID` et `PetCareWorker.ANIMAL_NAME` via `workDataOf` avec les valeurs de l’animal sélectionné. Ajoutez le tag `petcare-demo`. Envoyez la demande avec `WorkManager.getInstance(this).enqueueUniqueWork`, le nom `WORK_NAME` et la politique `ExistingWorkPolicy.KEEP`. Affichez ensuite le Toast `work_requested`.

### C. `PetCareWorker.doWork()`

Lisez l’ID et le nom dans `inputData` avec les constantes fournies. Un ID absent/non positif ou un nom absent/blanc doit retourner `Result.failure()`. Sinon, écrivez une trace `Log.i` avec le tag `PetCareWork` et retournez `Result.success` contenant ces mêmes données via `workDataOf`.

Le Worker du starter retourne actuellement un échec explicite et le bouton ne planifie rien avant modification. Votre travail est une simulation **locale**, sans réseau ni données médicales. N’ajoutez pas de serveur, de `Service`, de notification différée ou de délai bloquant.

**Réussite :** en anglais, les quatre textes sont traduits et interpolent correctement le nom ; en français, l’app reste correcte. Après « Planifier », `workText` arrive à `SUCCEEDED` avec le bon nom et Logcat contient la trace. Les états intermédiaires peuvent être très courts. Recommencez pour l’autre animal une fois le premier travail terminé. Avec `KEEP` et une file globale, un nouveau clic pendant un travail inachevé est ignoré ; aucune heure d’exécution précise n’est garantie.

## Après les exercices

Enregistrez vos modifications par un commit et poussez vers votre fork. Gardez les trois étapes lisibles dans votre historique. Répondez ensuite aux quiz/questionnaires indiqués par le formateur sur le LMS.

Les compteurs sont volontairement en mémoire : rotation ou fermeture peuvent les réinitialiser. L’état UI sera abordé en M2, les données durables en M3. M2 ajoutera les interfaces, la navigation, les listes et l’accessibilité ; M4 le diagnostic, M5 les tests. Les tests déjà fournis vérifient le matériel de départ : leur réussite ne signifie pas que vous avez terminé les exercices.

## En cas de blocage

Vérifiez le dossier ouvert, la fin de synchronisation, le SDK 36, le Gradle JDK 17/21 et un émulateur démarré. Une erreur de téléchargement se traite en vérifiant la connexion, sans changer les versions au hasard. Montrez au formateur le premier message d’erreur complet et la commande exécutée. Ne supprimez pas vos sources pour corriger Gradle.
