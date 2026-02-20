package org.entreprise.exceptions;

/**
 * Joseph_com : Exception levée lors d'une tentative d'ajout d'un joueur déjà existant.
 * Un doublon est détecté si un joueur avec le même ID ou le même pseudo existe déjà.
 */
public class DuplicatePlayerException extends Exception {

    // Joseph_com : Constructeur avec message d'erreur personnalisé
    public DuplicatePlayerException(String message) {
        super(message);
    }

    // Joseph_com : Constructeur avec message + cause originale (chaînage d'exceptions)
    public DuplicatePlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}