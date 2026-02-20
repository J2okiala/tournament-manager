package org.entreprise.model;

import java.time.LocalDate;

/**
 * Joseph_com : Modèle représentant un match entre deux joueurs.
 * Contient les références aux deux joueurs et leurs scores respectifs.
 */
public class Match {

    // Joseph_com : Identifiant unique du match
    private int id;

    // Joseph_com : Premier joueur participant au match
    private Player player1;

    // Joseph_com : Second joueur participant au match
    private Player player2;

    // Joseph_com : Score obtenu par le joueur 1 durant ce match
    private int scorePlayer1;

    // Joseph_com : Score obtenu par le joueur 2 durant ce match
    private int scorePlayer2;

    // Joseph_com : Date à laquelle le match a eu lieu
    private LocalDate date;

    // ─────────────────────────────────────────────
    // Joseph_com : Constructeur complet
    // ─────────────────────────────────────────────
    /**
     * @param id           identifiant unique du match
     * @param player1      premier joueur
     * @param player2      second joueur
     * @param scorePlayer1 score du joueur 1
     * @param scorePlayer2 score du joueur 2
     * @param date         date du match
     */
    public Match(int id, Player player1, Player player2,
                 int scorePlayer1, int scorePlayer2, LocalDate date) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
        this.date = date;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Méthode utilitaire pour déterminer le gagnant du match
    // Retourne null en cas d'égalité
    // ─────────────────────────────────────────────
    public Player getWinner() {
        if (scorePlayer1 > scorePlayer2) return player1;
        if (scorePlayer2 > scorePlayer1) return player2;
        return null; // Joseph_com : Égalité → pas de gagnant
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Getters et Setters
    // ─────────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Player getPlayer1() { return player1; }
    public void setPlayer1(Player player1) { this.player1 = player1; }

    public Player getPlayer2() { return player2; }
    public void setPlayer2(Player player2) { this.player2 = player2; }

    public int getScorePlayer1() { return scorePlayer1; }
    public void setScorePlayer1(int scorePlayer1) { this.scorePlayer1 = scorePlayer1; }

    public int getScorePlayer2() { return scorePlayer2; }
    public void setScorePlayer2(int scorePlayer2) { this.scorePlayer2 = scorePlayer2; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    // ─────────────────────────────────────────────
    // Joseph_com : Affichage lisible du match pour la console
    // ─────────────────────────────────────────────
    @Override
    public String toString() {
        String winnerName = (getWinner() != null) ? getWinner().getNickname() : "Égalité";
        return String.format("Match{id=%d, %s vs %s, score=%d-%d, date=%s, gagnant=%s}",
                id,
                player1.getNickname(), player2.getNickname(),
                scorePlayer1, scorePlayer2,
                date,
                winnerName);
    }
}