package org.entreprise.exceptions;

/**
 * Joseph_com : Exception levée lorsqu'un match est invalide.
 * Cas d'usage : un joueur contre lui-même, des scores négatifs,
 * ou des références de joueurs nulles.
 */
public class InvalidMatchException extends Exception {

    // Joseph_com : Constructeur avec message d'erreur personnalisé
    public InvalidMatchException(String message) {
        super(message);
    }

    // Joseph_com : Constructeur avec message + cause originale (chaînage d'exceptions)
    public InvalidMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}