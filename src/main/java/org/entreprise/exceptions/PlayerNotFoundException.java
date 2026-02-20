package org.entreprise.exceptions;

/**
 * Joseph_com : Exception levée lorsqu'un joueur recherché est introuvable.
 * Utilisée dans les services et DAO lors d'une recherche par ID ou pseudo.
 */
public class PlayerNotFoundException extends Exception {

    // Joseph_com : Constructeur avec message d'erreur personnalisé
    public PlayerNotFoundException(String message) {
        super(message);
    }

    // Joseph_com : Constructeur avec message + cause originale (chaînage d'exceptions)
    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}