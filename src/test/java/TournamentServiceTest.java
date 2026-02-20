package org.entreprise.service;

import org.entreprise.exceptions.DuplicatePlayerException;
import org.entreprise.exceptions.InvalidMatchException;
import org.entreprise.model.Match;
import org.entreprise.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Joseph_com : Tests unitaires pour le Gestionnaire de Tournoi e-Sport.
 * Couvre :
 *  - L'ajout d'un joueur (cas nominal)
 *  - La gestion des doublons (pseudo identique)
 *  - La création d'un match invalide (même joueur, score négatif)
 *  - Le calcul métier (calculateScore via Scorable)
 */
class TournamentServiceTest {

    // Joseph_com : Services réinitialisés avant chaque test pour garantir l'isolation
    private PlayerService playerService;
    private MatchService matchService;

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : Initialisation avant chaque test
    // On repart d'un état vide (les fichiers CSV du dossier data/ peuvent exister,
    // mais les services chargent les données au démarrage)
    // ─────────────────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        playerService = new PlayerService();
        matchService  = new MatchService(playerService);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : TEST 1 - Ajout nominal d'un joueur
    // Vérifie que le joueur est bien créé avec les bonnes valeurs
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Ajout d'un joueur valide - doit réussir")
    void testAddPlayer_ShouldSucceed() throws DuplicatePlayerException {
        // Joseph_com : ARRANGE - données de test
        String nickname = "TestPlayer_" + System.currentTimeMillis(); // pseudo unique à chaque run
        int level = 5;
        int score = 100;

        // Joseph_com : ACT - action à tester
        Player result = playerService.addPlayer(nickname, level, score);

        // Joseph_com : ASSERT - vérifications
        assertNotNull(result, "Le joueur créé ne doit pas être null");
        assertEquals(nickname, result.getNickname(), "Le pseudo doit correspondre");
        assertEquals(level, result.getLevel(), "Le niveau doit correspondre");
        assertEquals(score, result.getScore(), "Le score doit correspondre");
        assertTrue(result.getId() > 0, "L'ID doit être un entier positif");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : TEST 2 - Gestion des doublons
    // Vérifie que l'ajout d'un joueur avec un pseudo déjà existant lève l'exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Ajout d'un joueur en doublon - doit lever DuplicatePlayerException")
    void testAddPlayer_DuplicateNickname_ShouldThrowException() throws DuplicatePlayerException {
        // Joseph_com : ARRANGE - on ajoute un premier joueur
        String nickname = "DuplicateTest_" + System.currentTimeMillis();
        playerService.addPlayer(nickname, 3, 50);

        // Joseph_com : ACT & ASSERT - le second ajout avec le même pseudo doit échouer
        DuplicatePlayerException exception = assertThrows(
                DuplicatePlayerException.class,
                () -> playerService.addPlayer(nickname, 5, 80),
                "Une DuplicatePlayerException doit être levée pour un pseudo identique"
        );

        // Joseph_com : Vérification que le message d'erreur est pertinent
        assertTrue(exception.getMessage().contains(nickname),
                "Le message d'exception doit mentionner le pseudo dupliqué");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : TEST 3 - Match invalide : un joueur contre lui-même
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Création d'un match avec le même joueur des deux côtés - doit lever InvalidMatchException")
    void testCreateMatch_SamePlayer_ShouldThrowInvalidMatchException() throws DuplicatePlayerException {
        // Joseph_com : ARRANGE - création d'un joueur unique
        String nick = "SoloPlayer_" + System.currentTimeMillis();
        Player player = playerService.addPlayer(nick, 4, 200);

        // Joseph_com : ACT & ASSERT - un joueur ne peut pas jouer contre lui-même
        assertThrows(
                InvalidMatchException.class,
                () -> matchService.createMatch(player.getId(), player.getId(), 10, 5),
                "Une InvalidMatchException doit être levée si les deux joueurs ont le même ID"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : TEST 4 - Match invalide : score négatif
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Création d'un match avec score négatif - doit lever InvalidMatchException")
    void testCreateMatch_NegativeScore_ShouldThrowInvalidMatchException()
            throws DuplicatePlayerException {
        // Joseph_com : ARRANGE - création de deux joueurs
        String suffix = String.valueOf(System.currentTimeMillis());
        Player p1 = playerService.addPlayer("PlayerA_" + suffix, 2, 100);
        Player p2 = playerService.addPlayer("PlayerB_" + suffix, 3, 150);

        // Joseph_com : ACT & ASSERT - un score négatif doit lever une exception
        assertThrows(
                InvalidMatchException.class,
                () -> matchService.createMatch(p1.getId(), p2.getId(), -5, 10),
                "Une InvalidMatchException doit être levée si un score est négatif"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : TEST 5 - Calcul métier via l'interface Scorable
    // Vérifie la formule : calculateScore() = score × level
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Calcul du score (Scorable) - doit retourner score × niveau")
    void testCalculateScore_ShouldReturnScoreTimesLevel() {
        // Joseph_com : ARRANGE - joueur avec des valeurs connues
        Player player = new Player(99, "TestScorablePlayer", 4, 50);

        // Joseph_com : ACT - appel de la méthode calculateScore()
        int calculated = player.calculateScore();

        // Joseph_com : ASSERT - on attend 50 × 4 = 200
        assertEquals(200, calculated,
                "calculateScore() doit retourner score (50) × level (4) = 200");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : TEST 6 - Joueur introuvable par ID
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Recherche d'un joueur inexistant - doit lever PlayerNotFoundException")
    void testFindById_NotFound_ShouldThrowException() {
        // Joseph_com : ACT & ASSERT - ID inexistant (valeur très grande)
        assertThrows(
                Playernotfoundexception.class,
                () -> playerService.findById(999999),
                "Une PlayerNotFoundException doit être levée pour un ID inexistant"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Joseph_com : TEST 7 - Création et résultat d'un match valide
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Création d'un match valide - le bon gagnant doit être désigné")
    void testCreateMatch_Valid_WinnerShouldBeCorrect()
            throws DuplicatePlayerException, Playernotfoundexception, InvalidMatchException {
        // Joseph_com : ARRANGE
        String suffix = String.valueOf(System.currentTimeMillis());
        Player p1 = playerService.addPlayer("Winner_" + suffix, 5, 300);
        Player p2 = playerService.addPlayer("Loser_" + suffix, 3, 100);

        // Joseph_com : ACT - joueur 1 gagne 15 à 5
        Match match = matchService.createMatch(p1.getId(), p2.getId(), 15, 5);

        // Joseph_com : ASSERT
        assertNotNull(match, "Le match créé ne doit pas être null");
        assertNotNull(match.getWinner(), "Il doit y avoir un gagnant");
        assertEquals(p1.getId(), match.getWinner().getId(),
                "Le joueur 1 doit être désigné gagnant");
    }
}