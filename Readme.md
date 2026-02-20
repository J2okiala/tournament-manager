# 🎮 Gestionnaire de Tournoi e-Sport

Application console Java pour gérer un tournoi e-Sport : joueurs, matchs, scores et statistiques.

---

## Prérequis

| Outil | Version minimum |
|-------|----------------|
| Java  | 21              |
| Maven | 3.8+            |
| IDE   | IntelliJ IDEA   |

---

## Lancer l'application

### 1. Ouvrir le projet
Dans IntelliJ : `File` → `Open` → sélectionner le dossier `tournament-manager/`

> IntelliJ va automatiquement détecter le `pom.xml` et télécharger les dépendances.

### 2. Compiler et lancer
Clic droit sur `Main.java` → **Run 'Main'**

Ou via le terminal Maven :
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.joseph.tournament.Main"
```

### 3. Lancer les tests
Clic droit sur `TournamentServiceTest.java` → **Run 'TournamentServiceTest'**

Ou via Maven :
```bash
mvn test
```

---

## 📁 Structure du projet

```
tournament-manager/
├── pom.xml                                         ← Configuration Maven (Java 21, dépendances)
├── data/                                           ← Fichiers CSV générés automatiquement
│   ├── players.csv                                 ← Sauvegarde des joueurs
│   └── matches.csv                                 ← Sauvegarde des matchs
├── logs/                                           ← Fichiers de logs générés automatiquement
│   └── tournament.log
└── src/
    ├── main/
    │   ├── java/com/joseph/tournament/
    │   │   ├── Main.java                           ← Point d'entrée, menu interactif
    │   │   ├── interfaces/
    │   │   │   └── Scorable.java                   ← Interface de calcul de score
    │   │   ├── model/
    │   │   │   ├── Player.java                     ← Modèle joueur
    │   │   │   └── Match.java                      ← Modèle match
    │   │   ├── exceptions/
    │   │   │   ├── PlayerNotFoundException.java
    │   │   │   ├── DuplicatePlayerException.java
    │   │   │   └── InvalidMatchException.java
    │   │   ├── dao/
    │   │   │   ├── PlayerDAO.java                  ← Lecture/écriture players.csv
    │   │   │   └── MatchDAO.java                   ← Lecture/écriture matches.csv
    │   │   └── service/
    │   │       ├── PlayerService.java              ← Logique métier joueurs
    │   │       └── MatchService.java               ← Logique métier matchs
    │   └── resources/
    │       └── logback.xml                         ← Configuration des logs
    └── test/
        └── java/com/joseph/tournament/service/
            └── TournamentServiceTest.java          ← 7 tests unitaires JUnit 5
```

---

## Menu de l'application

Au démarrage, le menu suivant s'affiche :

```
╔══════════════════════════════════════════════╗
║   🎮  GESTIONNAIRE DE TOURNOI E-SPORT  🎮    ║
║          Développé avec Java 21              ║
╚══════════════════════════════════════════════╝

┌─────────────────────────────┐
│         MENU PRINCIPAL       │
├─────────────────────────────┤
│  1. ➕ Ajouter un joueur     │
│  2. 👥 Afficher les joueurs  │
│  3. ⚔️  Créer un match       │
│  4. 📋 Afficher les matchs   │
│  5. 📊 Statistiques          │
│  0. 🚪 Quitter               │
└─────────────────────────────┘
```

---

## ⚙️ Fonctionnalités détaillées

### Option 1 — Ajouter un joueur
- Saisir un pseudo (doit être unique)
- Saisir un niveau (ex : 1 à 10)
- Saisir un score initial
- Le joueur est sauvegardé immédiatement dans `data/players.csv`

### Option 2 — Afficher les joueurs
- Liste tous les joueurs triés par score décroissant
- Affiche le **score calculé** : `score × niveau` (formule de l'interface `Scorable`)

###  Option 3 — Créer un match
- Affiche la liste des joueurs disponibles avec leurs IDs
- Saisir l'ID du joueur 1, l'ID du joueur 2, et les deux scores
- Le match est sauvegardé dans `data/matches.csv`
- Validations : un joueur ne peut pas jouer contre lui-même, les scores ne peuvent pas être négatifs

### Option 4 — Afficher les matchs
- Liste tous les matchs enregistrés
- Affiche le gagnant 🏆 ou indique une égalité 🤝

### Option 5 — Statistiques
- Nombre total de joueurs et de matchs
- Score total et score moyen de tous les joueurs
- Total des points joués dans tous les matchs
- **Top 3 🥇🥈🥉** des meilleurs joueurs selon leur score calculé

---

## Sauvegarde des données (CSV)

Les données sont **automatiquement sauvegardées** à chaque action et **rechargées au démarrage**.

**Format `players.csv` :**
```
id,nickname,level,score
1,Shadow,8,450
2,NightOwl,5,200
```

**Format `matches.csv` :**
```
id,player1Id,player2Id,scorePlayer1,scorePlayer2,date
1,1,2,15,8,2026-02-20
```

---

## Logs

Les logs sont affichés dans la console ET écrits dans `logs/tournament.log`.

| Niveau  | Usage                                      |
|---------|--------------------------------------------|
| `INFO`  | Actions normales (ajout joueur, sauvegarde)|
| `WARN`  | Situations anormales (doublon, fichier absent) |
| `ERROR` | Erreurs critiques (problème de lecture CSV)|

---

## Tests unitaires

7 tests couvrent les cas principaux :

| Test | Description |
|------|-------------|
| `testAddPlayer_ShouldSucceed` | Ajout nominal d'un joueur |
| `testAddPlayer_DuplicateNickname_ShouldThrowException` | Pseudo déjà utilisé |
| `testCreateMatch_SamePlayer_ShouldThrowInvalidMatchException` | Joueur contre lui-même |
| `testCreateMatch_NegativeScore_ShouldThrowInvalidMatchException` | Score négatif |
| `testCalculateScore_ShouldReturnScoreTimesLevel` | Calcul métier Scorable |
| `testFindById_NotFound_ShouldThrowException` | Joueur introuvable par ID |
| `testCreateMatch_Valid_WinnerShouldBeCorrect` | Gagnant correctement désigné |

---

##  Dépendances

| Bibliothèque | Version | Rôle |
|---|---|---|
| `slf4j-api` | 2.0.9 | API de logging |
| `logback-classic` | 1.4.14 | Implémentation des logs |
| `junit-jupiter` | 5.10.1 | Tests unitaires |

---

## Auteur

Projet réalisé par Joseph developpeur fullstack dans le cadre d'un TP Java — Architecture POO, exceptions, streams, logs et tests unitaires.