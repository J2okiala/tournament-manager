package org.entreprise.service;

import org.entreprise.dao.PlayerDAO;
import org.entreprise.exceptions.DuplicatePlayerException;
import org.entreprise.exceptions.PlayerNotFoundException;
import org.entreprise.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Joseph_com : Service métier pour la gestion des joueurs.
 * Contient toute la logique applicative liée aux joueurs :
 * ajout, recherche, tri, statistiques.
 * Délègue la persistance au PlayerDAO.
 */
public class PlayerService {

    // Joseph_com : Logger pour tracer les actions métier sur les joueurs
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    // Joseph_com : DAO pour la persistence des joueurs en CSV
    private final PlayerDAO playerDAO;

    // Joseph_com : Liste en mémoire des joueurs (cache applicatif)
    private List<Player> players;

    // Joseph_com : Compteur auto-incrémenté pour générer les IDs uniques
    private int nextId;

    // ─────────────────────────────────────────────
    // Joseph_com : Constructeur - initialise le service et charge les données existantes
    // ─────────────────────────────────────────────
    public PlayerService() {
        this.playerDAO = new PlayerDAO();
        this.players = new ArrayList<>(playerDAO.loadAll());
        // Joseph_com : nextId = max(id existants) + 1 pour éviter les conflits d'ID
        this.nextId = players.stream()
                .mapToInt(Player::getId)
                .max()
                .orElse(0) + 1;
        logger.info("PlayerService initialisé avec {} joueur(s). Prochain ID : {}", players.size(), nextId);
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Ajoute un nouveau joueur avec validation anti-doublon
    // ─────────────────────────────────────────────
    /**
     * @param nickname pseudo du joueur (doit être unique)
     * @param level    niveau du joueur (doit être > 0)
     * @param score    score initial du joueur (doit être >= 0)
     * @return le joueur créé
     * @throws DuplicatePlayerException si le pseudo est déjà utilisé
     */
    public Player addPlayer(String nickname, int level, int score) throws DuplicatePlayerException {
        logger.info("Tentative d'ajout du joueur '{}'", nickname);

        // Joseph_com : Vérification de doublon par pseudo (insensible à la casse)
        boolean alreadyExists = players.stream()
                .anyMatch(p -> p.getNickname().equalsIgnoreCase(nickname));

        if (alreadyExists) {
            logger.warn("Doublon détecté : le pseudo '{}' existe déjà.", nickname);
            throw new DuplicatePlayerException("Un joueur avec le pseudo '" + nickname + "' existe déjà.");
        }

        // Joseph_com : Création du joueur avec un ID auto-généré
        Player newPlayer = new Player(nextId++, nickname, level, score);
        players.add(newPlayer);

        // Joseph_com : Persistance immédiate après chaque ajout
        playerDAO.saveAll(players);

        logger.info("Joueur '{}' ajouté avec succès (ID: {}).", nickname, newPlayer.getId());
        return newPlayer;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Recherche un joueur par son ID
    // ─────────────────────────────────────────────
    /**
     * @param id identifiant unique du joueur
     * @return le joueur correspondant
     * @throws PlayerNotFoundException si aucun joueur ne correspond à cet ID
     */
    public Player findById(int id) throws PlayerNotFoundException {
        return players.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Joueur introuvable avec l'ID : {}", id);
                    return new PlayerNotFoundException("Aucun joueur trouvé avec l'ID : " + id);
                });
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Retourne la liste de tous les joueurs triés par score décroissant
    // Utilise l'API Stream de Java pour le tri
    // ─────────────────────────────────────────────
    public List<Player> getAllPlayersSortedByScore() {
        logger.info("Récupération de tous les joueurs triés par score.");
        return players.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Retourne le top 3 des joueurs selon leur score calculé (via Scorable)
    // ─────────────────────────────────────────────
    public List<Player> getTop3Players() {
        logger.info("Calcul du top 3 des joueurs.");
        return players.stream()
                .sorted(Comparator.comparingInt(Player::calculateScore).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Calcule le score total de tous les joueurs (statistique globale)
    // Utilise reduce() via mapToInt pour agréger les valeurs
    // ─────────────────────────────────────────────
    public int getTotalScore() {
        int total = players.stream()
                .mapToInt(Player::getScore)
                .sum();
        logger.info("Score total calculé : {}", total);
        return total;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Calcule le score moyen de tous les joueurs
    // ─────────────────────────────────────────────
    public double getAverageScore() {
        return players.stream()
                .mapToInt(Player::getScore)
                .average()
                .orElse(0.0);
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Retourne la liste brute de tous les joueurs (non triée)
    // ─────────────────────────────────────────────
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }
}
