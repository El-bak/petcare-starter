# Repartir de la base officielle M2

À utiliser après l’annonce de publication de `m2`. Cette base contient M1 corrigé. Elle ne dépend pas de votre réussite des exercices précédents.

## Conserver M1

Dans votre clone, consultez `git status`. Pour garder vos modifications, créez une branche personnelle :

```powershell
git switch -c mon-travail-m1
git status
```

Ajoutez les fichiers souhaités avec `git add CHEMIN_DU_FICHIER`, puis `git commit -m "Conserver mon travail M1"`. N’ajoutez ni secrets ni `local.properties`. Facultativement, `git push -u origin mon-travail-m1` sauvegarde cette branche sur votre fork. Choisissez un nom libre si celui-ci existe déjà. Avant de continuer, `git status` doit être propre ; ne supprimez pas votre travail pour forcer le changement.

## Récupérer la base commune

```powershell
git remote -v
```

`origin` doit être votre fork. Si `upstream` n’existe pas, ajoutez-le :

```powershell
git remote add upstream https://github.com/ZakyB/petcare-starter.git
```

S’il existe déjà, vérifiez qu’il vise le dépôt officiel ; ne remplacez pas aveuglément une adresse. Puis :

```powershell
git fetch upstream
git switch -c m2 --track upstream/m2
git push -u origin m2
```

Vous récupérez la branche officielle, créez votre base locale et la publiez sur votre fork. Aucun merge ni cherry-pick de M1 n’est nécessaire.

Si une branche locale `m2` existe déjà, préservez-la et choisissez un autre nom libre :

```powershell
git switch -c m2-officiel --track upstream/m2
git push -u origin m2-officiel
```

N’utilisez ni `reset --hard` ni push forcé.

## Reprendre Android Studio

Attendez la synchronisation Gradle, reconstruisez et lancez. L’indicateur « À compléter » et la liste provisoire sont attendus. Lisez le README de cette branche pour les quatre exercices M2. Votre travail M1 reste disponible avec `git switch mon-travail-m1`, après avoir commité M2.
