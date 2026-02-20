package org.entreprise;

import org.entreprise.exceptions.DuplicatePlayerException;
import org.entreprise.exceptions.InvalidMatchException;
import org.entreprise.exceptions.PlayerNotFoundException;
import org.entreprise.model.Match;
import org.entreprise.model.Player;
import org.entreprise.service.MatchService;
import org.entreprise.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Joseph_com : Point d'entrée principal de l'application.
 * Gère le menu interactif en boucle et délègue les actions aux services métier.
 * L'application charge automatiquement les données CSV au démarrage.
 */
public class Main {

    // Joseph_com : Logger pour tracer les événements de la session principale
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Joseph_com : Scanner global partagé pour toutes les lectures console
    private static final Scanner scanner = new Scanner(System.in);

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Point d'entrée du programme
    // Instancie les services et lance la boucle principale du menu
    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        logger.info("=== Démarrage de l'application Gestionnaire de Tournoi e-Sport ===");

        // Joseph_com : Instanciation des services (le chargement CSV se fait dans le constructeur)
        PlayerService playerService = new PlayerService();
        MatchService matchService   = new MatchService(playerService);

        printWelcomeBanner();

        // Joseph_com : Boucle principale du menu
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntInput("Votre choix : ");

            switch (choice) {
                case 1 -> handleAddPlayer(playerService);
                case 2 -> handleDisplayPlayers(playerService);
                case 3 -> handleCreateMatch(playerService, matchService);
                case 4 -> handleDisplayMatches(matchService);
                case 5 -> handleDisplayStatistics(playerService, matchService);
                case 0 -> {
                    System.out.println("\n👋 Au revoir ! À bientôt dans l'arène !");
                    logger.info("Application fermée par l'utilisateur.");
                    running = false;
                }
                default -> {
                    System.out.println("⚠️  Choix invalide. Veuillez entrer un nombre entre 0 et 5.");
                    logger.warn("Choix de menu invalide : {}", choice);
                }
            }
        }

        scanner.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Affiche la bannière de bienvenue au lancement
    // ─────────────────────────────────────────────────────────────────────────
    private static void printWelcomeBanner() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║   🎮  GESTIONNAIRE DE TOURNOI E-SPORT  🎮    ║");
        System.out.println("║          Développé avec Java 21              ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Affiche le menu principal des options
    // ─────────────────────────────────────────────────────────────────────────
    private static void printMenu() {
        System.out.println("\n┌─────────────────────────────┐");
        System.out.println("│         MENU PRINCIPAL       │");
        System.out.println("├─────────────────────────────┤");
        System.out.println("│  1.  Ajouter un joueur     │");
        System.out.println("│  2.  Afficher les joueurs  │");
        System.out.println("│  3.  Créer un match       │");
        System.out.println("│  4.  Afficher les matchs   │");
        System.out.println("│  5.  Statistiques          │");
        System.out.println("│  0.  Quitter               │");
        System.out.println("└─────────────────────────────┘");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Gestion de l'ajout d'un joueur (option 1)
    // Lit les données au clavier et appelle le service
    // ─────────────────────────────────────────────────────────────────────────
    private static void handleAddPlayer(PlayerService playerService) {
        System.out.println("\n--- ➕ AJOUTER UN JOUEUR ---");

        System.out.print("Pseudo du joueur : ");
        String nickname = scanner.nextLine().trim();

        // Joseph_com : Validation : le pseudo ne peut pas être vide
        if (nickname.isEmpty()) {
            System.out.println("❌ Le pseudo ne peut pas être vide.");
            return;
        }

        int level = readIntInput("Niveau (1-10) : ");
        int score = readIntInput("Score initial : ");

        try {
            Player newPlayer = playerService.addPlayer(nickname, level, score);
            System.out.println("✅ Joueur ajouté : " + newPlayer);
        } catch (DuplicatePlayerException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Gestion de l'affichage des joueurs (option 2)
    // Affiche tous les joueurs triés par score décroissant
    // ─────────────────────────────────────────────────────────────────────────
    private static void handleDisplayPlayers(PlayerService playerService) {
        System.out.println("\n--- 👥 LISTE DES JOUEURS (triés par score) ---");
        List<Player> players = playerService.getAllPlayersSortedByScore();

        if (players.isEmpty()) {
            System.out.println("Aucun joueur enregistré pour le moment.");
            return;
        }

        System.out.printf("%-5s %-20s %-8s %-10s %-15s%n",
                "ID", "Pseudo", "Niveau", "Score", "Score calculé");
        System.out.println("─".repeat(60));

        // Joseph_com : Affichage de chaque joueur avec son score calculé (via Scorable)
        for (Player p : players) {
            System.out.printf("%-5d %-20s %-8d %-10d %-15d%n",
                    p.getId(), p.getNickname(), p.getLevel(),
                    p.getScore(), p.calculateScore());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Gestion de la création d'un match (option 3)
    // ─────────────────────────────────────────────────────────────────────────
    private static void handleCreateMatch(PlayerService playerService, MatchService matchService) {
        System.out.println("\n--- ⚔️  CRÉER UN MATCH ---");

        // Joseph_com : Afficher les joueurs disponibles pour faciliter la saisie
        List<Player> players = playerService.getAllPlayers();
        if (players.size() < 2) {
            System.out.println("❌ Il faut au moins 2 joueurs pour créer un match.");
            return;
        }

        System.out.println("Joueurs disponibles :");
        players.forEach(p -> System.out.printf("  [%d] %s (niveau %d)%n",
                p.getId(), p.getNickname(), p.getLevel()));

        int player1Id    = readIntInput("ID du joueur 1 : ");
        int player2Id    = readIntInput("ID du joueur 2 : ");
        int scorePlayer1 = readIntInput("Score du joueur 1 : ");
        int scorePlayer2 = readIntInput("Score du joueur 2 : ");

        try {
            Match newMatch = matchService.createMatch(player1Id, player2Id, scorePlayer1, scorePlayer2);
            System.out.println("✅ Match créé : " + newMatch);
        } catch (PlayerNotFoundException e) {
            System.out.println("❌ Joueur introuvable : " + e.getMessage());
        } catch (InvalidMatchException e) {
            System.out.println("❌ Match invalide : " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Gestion de l'affichage des matchs (option 4)
    // ─────────────────────────────────────────────────────────────────────────
    private static void handleDisplayMatches(MatchService matchService) {
        System.out.println("\n--- 📋 LISTE DES MATCHS ---");
        List<Match> matches = matchService.getAllMatches();

        if (matches.isEmpty()) {
            System.out.println("Aucun match enregistré pour le moment.");
            return;
        }

        // Joseph_com : Affichage de chaque match avec gagnant mis en évidence
        for (Match m : matches) {
            Player winner = m.getWinner();
            String winnerDisplay = (winner != null) ? "🏆 " + winner.getNickname() : "🤝 Égalité";

            System.out.printf("[Match #%d] %s %d - %d %s | %s | Gagnant : %s%n",
                    m.getId(),
                    m.getPlayer1().getNickname(), m.getScorePlayer1(),
                    m.getScorePlayer2(), m.getPlayer2().getNickname(),
                    m.getDate(),
                    winnerDisplay);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Gestion de l'affichage des statistiques (option 5)
    // Affiche le top 3 et les statistiques globales via les streams
    // ─────────────────────────────────────────────────────────────────────────
    private static void handleDisplayStatistics(PlayerService playerService, MatchService matchService) {
        System.out.println("\n--- 📊 STATISTIQUES DU TOURNOI ---");

        // Joseph_com : Statistiques globales des joueurs
        System.out.println("\n🎯 Statistiques globales :");
        System.out.println("  Nombre de joueurs    : " + playerService.getAllPlayers().size());
        System.out.println("  Score total global   : " + playerService.getTotalScore());
        System.out.printf( "  Score moyen          : %.2f%n", playerService.getAverageScore());
        System.out.println("  Nombre de matchs     : " + matchService.getAllMatches().size());
        System.out.println("  Points joués (matchs): " + matchService.getTotalPointsPlayed());

        // Joseph_com : Top 3 des joueurs triés par score calculé (Scorable)
        System.out.println("\n🏆 TOP 3 des joueurs (score calculé = score × niveau) :");
        List<Player> top3 = playerService.getTop3Players();

        if (top3.isEmpty()) {
            System.out.println("  Aucun joueur enregistré.");
            return;
        }

        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < top3.size(); i++) {
            Player p = top3.get(i);
            System.out.printf("  %s %s — Score calculé : %d (score: %d × niveau: %d)%n",
                    medals[i], p.getNickname(), p.calculateScore(), p.getScore(), p.getLevel());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Méthode utilitaire pour lire un entier au clavier en toute sécurité
    // Redemande la saisie si l'utilisateur entre une valeur non numérique
    // ─────────────────────────────────────────────────────────────────────────
    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Veuillez entrer un nombre entier valide.");
            }
        }
    }
}