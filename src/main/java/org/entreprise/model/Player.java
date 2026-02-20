package org.entreprise.model;
import org.entreprise.interfaces.Scorable;

/**
 * Joseph_com : Modèle représentant un joueur du tournoi.
 * Implémente l'interface Scorable pour calculer son score pondéré par son niveau.
 */
public class Player implements Scorable {

    // Joseph_com : Identifiant unique du joueur
    private int id;

    // Joseph_com : Pseudo/surnom du joueur
    private String nickname;

    // Joseph_com : Niveau du joueur (1 = débutant, 10 = expert)
    private int level;

    // Joseph_com : Score brut accumulé par le joueur
    private int score;

    // ─────────────────────────────────────────────
    // Joseph_com : Constructeur complet
    // ─────────────────────────────────────────────
    /**
     * @param id       identifiant unique
     * @param nickname pseudo du joueur
     * @param level    niveau du joueur
     * @param score    score brut du joueur
     */
    public Player(int id, String nickname, int level, int score) {
        this.id = id;
        this.nickname = nickname;
        this.level = level;
        this.score = score;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Implémentation de l'interface Scorable
    // Le score calculé tient compte du niveau du joueur (multiplicateur)
    // ─────────────────────────────────────────────
    @Override
    public int calculateScore() {
        // Joseph_com : Formule : score brut * niveau → récompense les joueurs expérimentés
        return this.score * this.level;
    }

    // ─────────────────────────────────────────────
    // Joseph_com : Getters et Setters
    // ─────────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    // ─────────────────────────────────────────────
    // Joseph_com : Méthode toString pour l'affichage console
    // ─────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Player{id=%d, nickname='%s', level=%d, score=%d, calculatedScore=%d}",
                id, nickname, level, score, calculateScore());
    }

    // ─────────────────────────────────────────────
    // Joseph_com : equals et hashCode basés sur l'id pour comparer les joueurs
    // ─────────────────────────────────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
