import project.*;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Demander le nom de l'utilisateur
        String playerName = JOptionPane.showInputDialog(null, "Veuillez saisir votre nom :", "Nom d'utilisateur", JOptionPane.PLAIN_MESSAGE);
        String enemyName ="Enemi";

        // Vérifier si le nom est valide
        if (playerName != null && !playerName.isEmpty()) {
            // Affichage des noms et prénoms du binôme


            System.out.println("Joueurs : [" + playerName + "], [" + enemyName + "]");
            // Créer les monstres disponibles
            List<Monster> availableMonsters = MonsterFactory.createMonsters();
            // Créer les decks du joueur et de l'ennemi
            Deck playerDeck = new Deck(availableMonsters);
            Deck enemyDeck = new Deck(availableMonsters);



            // Création de la fenêtre de jeu
            SwingUtilities.invokeLater(() -> {
                GameWindow window = new GameWindow(playerDeck, enemyDeck, playerName, enemyName);
                window.setVisible(true);

                //Le jeu commence avec le tour du joueur
            });
        } else {
            System.out.println("Nom invalide. Le jeu ne peut pas démarrer.");
        }
    }
}

