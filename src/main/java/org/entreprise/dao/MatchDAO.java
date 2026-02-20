package org.entreprise.dao;

import org.entreprise.model.Match;
import org.entreprise.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Joseph_com : DAO (Data Access Object) pour la gestion des matchs.
 * Responsable de la lecture et de l'écriture des matchs dans le fichier CSV.
 * Format CSV : id,player1Id,player2Id,scorePlayer1,scorePlayer2,date
 */
public class MatchDAO {

    // Joseph_com : Logger dédié à cette classe pour tracer les opérations fichier
    private static final Logger logger = LoggerFactory.getLogger(MatchDAO.class);

    // Joseph_com : Chemin vers le fichier CSV des matchs
    private static final String FILE_PATH = "data/matches.csv";

    // Joseph_com : En-tête du fichier CSV
    private static final String CSV_HEADER = "id,player1Id,player2Id,scorePlayer1,scorePlayer2,date";

    // ─────────────────────────────────────────────
    // Joseph_com : Constructeur - s'assure que le dossier data/ existe
    // ─────────────────────────────────────────────
    public MatchDAO() {
        createDataDirectoryIfNeeded();
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Crée le répertoire data/ s'il n'existe pas encore
    // ─────────────────────────────────────────────
    private void createDataDirectoryIfNeeded() {
        File dir = new File("data");
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                logger.info("Dossier 'data/' créé avec succès.");
            } else {
                logger.warn("Impossible de créer le dossier 'data/'.");
            }
        }
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Sauvegarde la liste complète des matchs dans le fichier CSV
    // On stocke les IDs des joueurs (pas les objets entiers) pour éviter la redondance
    // ─────────────────────────────────────────────
    /**
     * @param matches liste de matchs à sauvegarder
     */
    public void saveAll(List<Match> matches) {
        logger.info("Sauvegarde de {} match(s) dans '{}'", matches.size(), FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Joseph_com : Écriture de l'en-tête CSV
            writer.write(CSV_HEADER);
            writer.newLine();

            // Joseph_com : Écriture de chaque match ligne par ligne
            for (Match match : matches) {
                writer.write(matchToCsv(match));
                writer.newLine();
            }

            logger.info("Sauvegarde CSV des matchs réussie.");

        } catch (IOException e) {
            logger.error("Erreur lors de la sauvegarde des matchs : {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Charge tous les matchs depuis le fichier CSV
    // Nécessite la liste des joueurs déjà chargés pour reconstituer les références
    // ─────────────────────────────────────────────
    /**
     * @param availablePlayers liste des joueurs déjà chargés (pour relier les IDs)
     * @return liste de tous les matchs chargés depuis le CSV
     */
    public List<Match> loadAll(List<Player> availablePlayers) {
        List<Match> matches = new ArrayList<>();
        File file = new File(FILE_PATH);

        // Joseph_com : Si le fichier n'existe pas, on retourne une liste vide
        if (!file.exists()) {
            logger.warn("Fichier '{}' introuvable. Démarrage avec une liste de matchs vide.", FILE_PATH);
            return matches;
        }

        logger.info("Chargement des matchs depuis '{}'", FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                // Joseph_com : Ignorer la première ligne (en-tête CSV)
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Joseph_com : Ignorer les lignes vides
                if (line.trim().isEmpty()) continue;

                try {
                    Match match = csvToMatch(line, availablePlayers);
                    matches.add(match);
                } catch (Exception e) {
                    logger.error("Ligne CSV de match invalide ignorée : '{}' - Erreur : {}", line, e.getMessage());
                }
            }

            logger.info("{} match(s) chargé(s) avec succès.", matches.size());

        } catch (IOException e) {
            logger.error("Erreur lors du chargement des matchs : {}", e.getMessage());
        }

        return matches;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Convertit un objet Match en ligne CSV
    // On sauvegarde les IDs des joueurs, pas leurs données complètes
    // ─────────────────────────────────────────────
    private String matchToCsv(Match match) {
        return match.getId() + "," +
                match.getPlayer1().getId() + "," +
                match.getPlayer2().getId() + "," +
                match.getScorePlayer1() + "," +
                match.getScorePlayer2() + "," +
                match.getDate().toString();
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Convertit une ligne CSV en objet Match
    // Recherche les joueurs correspondants par ID dans la liste fournie
    // ─────────────────────────────────────────────
    private Match csvToMatch(String line, List<Player> players) {
        String[] parts = line.split(",");

        if (parts.length != 6) {
            throw new IllegalArgumentException("Format CSV de match invalide, 6 colonnes attendues : " + line);
        }

        int id            = Integer.parseInt(parts[0].trim());
        int player1Id     = Integer.parseInt(parts[1].trim());
        int player2Id     = Integer.parseInt(parts[2].trim());
        int scorePlayer1  = Integer.parseInt(parts[3].trim());
        int scorePlayer2  = Integer.parseInt(parts[4].trim());
        LocalDate date    = LocalDate.parse(parts[5].trim());

        // Joseph_com : Recherche des joueurs par leur ID dans la liste en mémoire
        Player player1 = players.stream()
                .filter(p -> p.getId() == player1Id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable avec ID : " + player1Id));

        Player player2 = players.stream()
                .filter(p -> p.getId() == player2Id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable avec ID : " + player2Id));

        return new Match(id, player1, player2, scorePlayer1, scorePlayer2, date);
    }
}