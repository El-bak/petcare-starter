# PetCare — starter étudiant M2

Branche `m2` du [dépôt étudiant](https://github.com/ZakyB/petcare-starter). Kotlin avec **Android Views/XML**, sans Compose. Tous les exercices M1 sont corrigés : Toast, Snackbar annulable, notification immédiate, anglais et WorkManager fonctionnent. Les exercices M2 restent à réaliser. L’application démarre avec un layout vertical, un indicateur « À compléter », une liste en attente et une présentation provisoire. Ces états sont volontaires.

`main` et `m1` conservent le starter M1. Les instructions suivantes concernent uniquement `m2`.

## Récupérer le projet

Si vous avez déjà un fork/clone M1, suivez [Passer à M2](docs/PASSER-A-M2.md). Aucun merge de vos exercices M1 n’est nécessaire.

Pour un nouveau fork : ouvrez le dépôt officiel, cliquez sur **Fork** et désélectionnez « Copy the main branch only » pour inclure `m2`. Copiez l’URL de votre fork avec **Code**, puis :

```powershell
git clone --branch m2 URL_DE_VOTRE_FORK petcare-starter
cd petcare-starter
git remote -v
```

Remplacez `URL_DE_VOTRE_FORK` par votre URL réelle. `origin` doit pointer vers votre compte. Si votre fork ne contient que `main`, utilisez la procédure `upstream` du guide. Attendez l’annonce de publication de `m2` par le formateur.

## Configuration et premier lancement

Dans Android Studio, **Open** le dossier contenant `settings.gradle.kts`, pas seulement `app`. Ne créez pas un projet neuf. Attendez la synchronisation Gradle et les téléchargements ; Internet est nécessaire au premier lancement. Conservez les versions fixées.

| Élément | Configuration |
| --- | --- |
| Android Studio | Compatible avec AGP 8.13.2, par exemple Otter 2 (2025.2.2) |
| Gradle JDK | 17 ou 21, dans Settings > Build Tools > Gradle |
| Wrapper / AGP / Kotlin | 8.13 / 8.13.2 / 2.3.0 |
| SDK / Build Tools | Platform 36 / 35.0.0 |
| Minimum / cible | API 26 / 35 |
| Appareil conseillé | Émulateur API 35, image adaptée au processeur |

Installez le SDK via **SDK Manager**, démarrez un appareil via **Device Manager**, sélectionnez `app` et **Run**. Un téléphone API 26+ avec débogage USB convient aussi. Android Studio écrit `local.properties`, propre à votre machine et ignoré par Git. En terminal, configurez `ANDROID_HOME` si nécessaire. Ne copiez pas un chemin de SDK provenant d’un autre ordinateur.

À la racine, PowerShell :

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

Sur macOS/Linux : `chmod +x gradlew`, puis `./gradlew assembleDebug testDebugUnitTest lintDebug`. L’APK est `app/build/outputs/apk/debug/app-debug.apk`. Compiler n’installe pas l’app : utilisez **Run**. Les tests fournis vérifient le socle ; leur réussite ne valide pas les exercices M2.

## Repères et infrastructure fournie

Les chemins Kotlin suivants sont relatifs à `app/src/main/java/com/example/petcare/` ; les ressources à `app/src/main/res/`.

| Fichier | Rôle |
| --- | --- |
| `MainActivity.kt` | Carnet, acquis M1, traces `PetCareCycle` et fonctions M2 à compléter |
| `model/Animal.kt`, `model/CompteurSoins.kt` | Objets animaux et compteurs ; `snapshot()` et `restaurer(Map<Long, Int>)` fournis |
| `data/DonneesAnimaux.kt` | 60 animaux fictifs en mémoire, IDs 1 à 60, Nala puis Oslo |
| `data/AnimalPagingSource.kt` | Squelette de pagination locale |
| `ui/AboutActivity.kt` | Activity déclarée, fermeture déjà câblée |
| `ui/CareProgressView.kt` | Champs, mesure et `setProgress()` fournis ; dessin et métadonnées à compléter |
| `ui/AnimalListActivity.kt`, `ui/AnimalAdapter.kt` | RecyclerView, Pager, DiffUtil, états de chargement et retour de sélection fournis |
| `ui/UiInsets.kt` | Marges des barres système fournies |
| `work/PetCareWorker.kt` | Vérification locale M1 corrigée |

`layout/activity_main.xml` contient le tiroir, le toolbar et un ScrollView incluant `content_home.xml`. Le formulaire est dans ce dernier. `activity_about.xml`, `activity_pending.xml`, `activity_animal_list.xml` et `item_animal.xml` existent déjà. Le manifeste déclare les deux nouvelles Activities non exportées. Les imports et dépendances nécessaires sont présents.

Les boutons directs **Animaux** (`animalsButton`) et **À propos** (`aboutButton`) fonctionnent avant l’exercice de navigation. Le menu et le tiroir s’ouvrent déjà ; leurs destinations restent à relier en exercice 4. Gardez ces raccourcis pendant les exercices.

## Exercice M2.1 — Activity, état et ConstraintLayout

1. Dans Logcat, filtrez `PetCareCycle`. Observez les callbacks à l’ouverture, au passage en arrière-plan et à la rotation. Distinguez `onPause`, `onStop` et `onDestroy`. Une destruction n’est pas une sauvegarde métier fiable.
2. Dans `ui/AboutActivity.afficherPresentation()`, remplacez le layout d’attente par `R.layout.activity_about`. `onCreate` gère déjà les marges et Fermer. Conservez les IDs `root`, `pageTitle` et `closeButton`.
3. Dans `layout/content_home.xml`, placez `careButton` et `snackButton` côte à côte, largeurs `0dp`, dans une chaîne horizontale entre les deux bords du parent. Leur haut doit être sous `nextButton`. Ajoutez un espace entre eux. La barrière `careActionsBottom` fournie garde les éléments suivants sous le bouton le plus grand. Gardez une hauteur adaptable et des cibles tactiles d’au moins 48dp.
4. Dans `MainActivity.kt`, complétez `enregistrerSession(sortie: Bundle)` et `restaurerSession(etat: Bundle?)`. Leurs appels sont fournis dans `onSaveInstanceState` et `onCreate`. Sauvegardez `indexAnimal` et tous les compteurs. `snapshot()` renvoie une copie `Map<Long, Int>`. Un `LongArray` d’IDs et un `IntArray` de valeurs, dans le même ordre, conviennent au Bundle. Gardez les mêmes clés à l’écriture et à la lecture. Gérez un Bundle nul, les clés absentes et un index hors limites ; restaurez via `compteurs.restaurer(...)`.

**Vérification :** ouvrez À propos puis fermez. Vérifiez les deux boutons sans chevauchement sur une petite fenêtre et avec une police agrandie. Ajoutez des soins à Nala et à Oslo ; faites pivoter l’app avec Oslo sélectionné. La sélection et les deux compteurs doivent être retrouvés. Le Bundle protège l’état UI lors d’une recréation, il ne remplace pas le stockage durable de M3.

## Exercice M2.2 — View personnalisée, thème et accessibilité

1. Complétez `ui/CareProgressView.onDraw(canvas)` : piste circulaire, arc depuis -90° et texte `progress_value` centré. `paint`, `bounds`, `nombre`, `objectif` sont fournis. Calculez la fraction en flottants et bornez-la à [0, 1] pour l’arc. Le texte garde le vrai compteur même au-delà de l’objectif. Réservez la moitié de l’épaisseur du trait dans les limites du cercle pour éviter les découpes. Utilisez la taille de la View, des dp pour le trait et des sp pour le texte.
2. Dans `values/theme_m2.xml`, reliez `colorPrimary`, `colorPrimaryVariant`, `colorOnPrimary`, `colorSecondary`, `colorOnSecondary` aux ressources correspondantes de `values/colors_m2.xml`. Le manifeste utilise déjà `Theme.PetCare.M2`. Les variantes `values-night/colors_m2.xml` sont fournies. Dans le dessin, récupérez les couleurs du thème via `MaterialColors.getColor`.
3. Dans `MainActivity.configurerAccessibilite()`, marquez `animalText` comme titre via `ViewCompat.setAccessibilityHeading`. Donnez à `animalInfoButton` une description contextualisée via `animal_info_description` et le nom courant. La fonction est rappelée à chaque sélection. Évitez de doubler le texte des boutons déjà libellés.
4. Dans `CareProgressView.onInitializeAccessibilityNodeInfo(info)`, après `super`, exposez le rôle `android.widget.ProgressBar` et une plage entière de 0 à `objectif`, avec la valeur courante bornée. Les imports `AccessibilityNodeInfoCompat` et son `RangeInfoCompat` sont présents. Le texte descriptif localisé est déjà actualisé par `setProgress()` ; conservez-le. L’indicateur est en lecture seule : aucune action tactile à inventer.

**Vérification :** 0, 1, 5 puis 6 soins : l’arc ne dépasse pas un tour et le texte continue à augmenter. Changez d’animal et contrôlez la description de l’icône. Activez thème sombre, police agrandie et TalkBack : contraste, ordre de lecture, titre, nom du bouton et progression doivent rester compréhensibles. Les tests de métadonnées ne remplacent pas une écoute avec TalkBack.

## Exercice M2.3 — RecyclerView et Paging local

1. Dans `data/AnimalPagingSource.load(params)`, remplacez la page vide par une portion de la liste fournie. `params.key == null` signifie page 0. Chaque page contient au plus `PAGE_SIZE` (10) éléments. Calculez des bornes sûres ; une liste vide donne une page terminale vide. `prevKey` vaut null pour la première page, `nextKey` vaut null à la fin. Une clé négative donne `LoadResult.Error`. Ici `loadSize` est une indication : le protocole reste à pages fixes de 10, avec `initialLoadSize` identique dans le Pager.
2. Complétez `getRefreshKey(state)` à partir de `anchorPosition` et `closestPageToPosition`. Ancre ou page absente : null. Retrouvez la page courante depuis sa clé précédente ou suivante. Le nom d’animal n’est pas une clé.
3. Dans `ui/AnimalAdapter.onBindViewHolder(holder, position)`, récupérez l’animal via `getItem(position)`, gérez son absence éventuelle et déléguez à `holder.afficher(animal)`. Cette méthode fournie remplit la ligne et relie la sélection.
4. Dans `ui/AnimalListActivity.connecterListe()`, lancez une coroutine dans `lifecycleScope`, collectez `pages` pendant `repeatOnLifecycle(Lifecycle.State.STARTED)` avec `collectLatest`, puis soumettez chaque `PagingData` via `adapter.submitData`. Imports, Pager, layout manager et états de chargement sont fournis. N’ajoutez pas un second adapter.

**Vérification :** ouvrez Animaux avec le bouton direct ; descendez jusqu’à l’ID 60, « Mina 60 », sans doublon ni saut. Sélectionnez un animal : il apparaît dans le carnet via le retour d’Activity fourni. Revenez dans la liste et faites pivoter : l’écran reste utilisable. Les données sont locales en mémoire : aucun serveur ni Room/DAO/Repository à ajouter.

## Exercice M2.4 — menu et tiroir

Dans `MainActivity.kt`, remplacez les attentes de `traiterActionMenu(item)` et `traiterDestination(item)` par un routage selon `item.itemId`.

| Origine | Identifiant | Action attendue |
| --- | --- | --- |
| `menu/main_menu.xml` | `action_animals` | `ouvrirAnimaux()` |
| Menu | `action_about` | `ouvrirPresentation()` |
| `menu/drawer_menu.xml` | `nav_home` | Rester sur le carnet |
| Tiroir | `nav_animals` | `ouvrirAnimaux()` |
| Tiroir | `nav_about` | `ouvrirPresentation()` |

Retournez true pour une action traitée et false pour un identifiant inconnu. Le menu est gonflé, les listeners sont installés, le tiroir se ferme après sélection et Retour ferme d’abord le tiroir ouvert. Réutilisez ces mécanismes sans relancer MainActivity pour Carnet.

**Vérification :** atteignez les deux pages via le menu puis via le tiroir. Retour depuis la liste et Fermer depuis À propos retrouvent le carnet. Retour avec le tiroir ouvert ferme celui-ci avant de quitter. La sélection d’un animal reste opérationnelle.

## Après la séance

Commitez votre travail et poussez vers **votre fork**. Les quiz seront organisés par le formateur sur Teams. M3 apportera Room/DAO/Repository et préférences ; M4 le diagnostic, M5 les exercices de tests. Les tests de socle fournis ne contiennent pas le corrigé M2.

En cas de blocage : vérifiez `git branch --show-current`, le dossier ouvert, la fin de synchronisation, le SDK 36, le JDK 17/21 et l’appareil. Montrez le premier message d’erreur complet au formateur. Ne changez pas les versions au hasard.
