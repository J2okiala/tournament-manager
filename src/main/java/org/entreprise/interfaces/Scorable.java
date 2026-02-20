package org.entreprise.interfaces;

/**
 * Joseph_com : Interface Scorable
 * Contrat que doivent respecter les classes capables de calculer un score.
 * Toute classe implémentant cette interface doit fournir sa propre logique de calcul.
 */
public interface Scorable {

    /**
     * Joseph_com : Calcule et retourne le score de l'objet.
     *
     * @return le score calculé sous forme d'entier
     */
    int calculateScore();
}