package org.entreprise.dao;

import org.entreprise.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Joseph_com : DAO (Data Access Object) pour la gestion des joueurs.
 * Responsable de la lecture et de l'écriture des joueurs dans le fichier CSV.
 * Format CSV : id,nickname,level,score
 */
public class PlayerDAO {

    // Joseph_com : Logger dédié à cette classe pour tracer les opérations fichier
    private static final Logger logger = LoggerFactory.getLogger(PlayerDAO.class);

    // Joseph_com : Chemin vers le fichier CSV des joueurs
    private static final String FILE_PATH = "data/players.csv";

    // Joseph_com : En-tête du fichier CSV
    private static final String CSV_HEADER = "id,nickname,level,score";

    // ─────────────────────────────────────────────
    // Joseph_com : Constructeur - s'assure que le dossier data/ existe au démarrage
    // ─────────────────────────────────────────────
    public PlayerDAO() {
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
    // Joseph_com : Sauvegarde la liste complète des joueurs dans le fichier CSV
    // Écrase le fichier existant à chaque sauvegarde (stratégie simple)
    // ─────────────────────────────────────────────
    /**
     * @param players liste de joueurs à sauvegarder
     */
    public void saveAll(List<Player> players) {
        logger.info("Sauvegarde de {} joueur(s) dans '{}'", players.size(), FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Joseph_com : Écriture de l'en-tête CSV
            writer.write(CSV_HEADER);
            writer.newLine();

            // Joseph_com : Écriture de chaque joueur ligne par ligne
            for (Player player : players) {
                writer.write(playerToCsv(player));
                writer.newLine();
            }

            logger.info("Sauvegarde CSV réussie.");

        } catch (IOException e) {
            logger.error("Erreur lors de la sauvegarde des joueurs : {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Charge tous les joueurs depuis le fichier CSV
    // Retourne une liste vide si le fichier n'existe pas encore
    // ─────────────────────────────────────────────
    /**
     * @return liste de tous les joueurs chargés depuis le CSV
     */
    public List<Player> loadAll() {
        List<Player> players = new ArrayList<>();
        File file = new File(FILE_PATH);

        // Joseph_com : Si le fichier n'existe pas, on retourne une liste vide
        if (!file.exists()) {
            logger.warn("Fichier '{}' introuvable. Démarrage avec une liste vide.", FILE_PATH);
            return players;
        }

        logger.info("Chargement des joueurs depuis '{}'", FILE_PATH);

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
                    Player player = csvToPlayer(line);
                    players.add(player);
                } catch (Exception e) {
                    logger.error("Ligne CSV invalide ignorée : '{}' - Erreur : {}", line, e.getMessage());
                }
            }

            logger.info("{} joueur(s) chargé(s) avec succès.", players.size());

        } catch (IOException e) {
            logger.error("Erreur lors du chargement des joueurs : {}", e.getMessage());
        }

        return players;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Convertit un objet Player en ligne CSV
    // Format : id,nickname,level,score
    // ─────────────────────────────────────────────
    private String playerToCsv(Player player) {
        return player.getId() + "," +
                player.getNickname() + "," +
                player.getLevel() + "," +
                player.getScore();
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Convertit une ligne CSV en objet Player
    // Lève une exception si le format est incorrect
    // ─────────────────────────────────────────────
    private Player csvToPlayer(String line) {
        String[] parts = line.split(",");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Format CSV invalide, 4 colonnes attendues : " + line);
        }

        int id       = Integer.parseInt(parts[0].trim());
        String nick  = parts[1].trim();
        int level    = Integer.parseInt(parts[2].trim());
        int score    = Integer.parseInt(parts[3].trim());

        return new Player(id, nick, level, score);
    }
}