package org.entreprise.service;

import org.entreprise.dao.MatchDAO;
import org.entreprise.exceptions.InvalidMatchException;
import org.entreprise.model.Match;
import org.entreprise.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Joseph_com : Service métier pour la gestion des matchs.
 * Contient toute la logique applicative liée aux matchs :
 * création, validation, affichage, statistiques.
 * Délègue la persistance au MatchDAO.
 */
public class MatchService {

    // Joseph_com : Logger pour tracer les actions métier sur les matchs
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    // Joseph_com : DAO pour la persistence des matchs en CSV
    private final MatchDAO matchDAO;

    // Joseph_com : Service joueurs pour retrouver les joueurs par ID
    private final PlayerService playerService;

    // Joseph_com : Liste en mémoire des matchs (cache applicatif)
    private List<Match> matches;

    // Joseph_com : Compteur auto-incrémenté pour générer les IDs de match uniques
    private int nextId;

    // ─────────────────────────────────────────────
    // Joseph_com : Constructeur - initialise le service et charge les données existantes
    // ─────────────────────────────────────────────
    public MatchService(PlayerService playerService) {
        this.playerService = playerService;
        this.matchDAO = new MatchDAO();
        // Joseph_com : Chargement des matchs en leur passant la liste des joueurs pour résoudre les IDs
        this.matches = new ArrayList<>(matchDAO.loadAll(playerService.getAllPlayers()));
        this.nextId = matches.stream()
                .mapToInt(Match::getId)
                .max()
                .orElse(0) + 1;
        logger.info("MatchService initialisé avec {} match(s). Prochain ID : {}", matches.size(), nextId);
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Crée un nouveau match avec validation des données
    // ─────────────────────────────────────────────
    /**
     * @param player1Id    ID du premier joueur
     * @param player2Id    ID du second joueur
     * @param scorePlayer1 score du joueur 1 (doit être >= 0)
     * @param scorePlayer2 score du joueur 2 (doit être >= 0)
     * @return le match créé
     * @throws Playernotfoundexception si un des deux joueurs est introuvable
     * @throws InvalidMatchException   si le match est invalide (joueur contre lui-même, score négatif)
     */
    public Match createMatch(int player1Id, int player2Id, int scorePlayer1, int scorePlayer2)
            throws Playernotfoundexception, InvalidMatchException {

        logger.info("Tentative de création d'un match : Joueur {} vs Joueur {}", player1Id, player2Id);

        // Joseph_com : Vérification : un joueur ne peut pas jouer contre lui-même
        if (player1Id == player2Id) {
            logger.warn("Match invalide : un joueur ne peut pas s'affronter lui-même (ID: {})", player1Id);
            throw new InvalidMatchException("Un joueur ne peut pas s'affronter lui-même.");
        }

        // Joseph_com : Vérification : les scores ne peuvent pas être négatifs
        if (scorePlayer1 < 0 || scorePlayer2 < 0) {
            logger.warn("Match invalide : les scores ne peuvent pas être négatifs ({}, {})", scorePlayer1, scorePlayer2);
            throw new InvalidMatchException("Les scores du match ne peuvent pas être négatifs.");
        }

        // Joseph_com : Récupération des joueurs (lève PlayerNotFoundException si introuvable)
        Player player1 = playerService.findById(player1Id);
        Player player2 = playerService.findById(player2Id);

        // Joseph_com : Création du match avec la date du jour
        Match newMatch = new Match(nextId++, player1, player2, scorePlayer1, scorePlayer2, LocalDate.now());
        matches.add(newMatch);

        // Joseph_com : Persistance immédiate après chaque match créé
        matchDAO.saveAll(matches);

        logger.info("Match créé avec succès : {} vs {} (ID: {})",
                player1.getNickname(), player2.getNickname(), newMatch.getId());

        return newMatch;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Retourne la liste de tous les matchs
    // ─────────────────────────────────────────────
    public List<Match> getAllMatches() {
        return new ArrayList<>(matches);
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Retourne les matchs d'un joueur spécifique (par ID)
    // Utilise les streams pour filtrer
    // ─────────────────────────────────────────────
    public List<Match> getMatchesByPlayer(int playerId) {
        return matches.stream()
                .filter(m -> m.getPlayer1().getId() == playerId
                        || m.getPlayer2().getId() == playerId)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Calcule le score total de tous les points joués dans les matchs
    // ─────────────────────────────────────────────
    public int getTotalPointsPlayed() {
        int total = matches.stream()
                .mapToInt(m -> m.getScorePlayer1() + m.getScorePlayer2())
                .sum();
        logger.info("Total des points joués dans tous les matchs : {}", total);
        return total;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Calcule le nombre total de victoires d'un joueur
    // ─────────────────────────────────────────────
    public long countWins(int playerId) {
        return matches.stream()
                .filter(m -> {
                    Player winner = m.getWinner();
                    return winner != null && winner.getId() == playerId;
                })
                .count();
    }
}
