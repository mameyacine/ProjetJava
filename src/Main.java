import project.*;



import javax.swing.SwingUtilities;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Affichage des noms et prénoms du binôme
        //System.out.println("Joueurs : [Nom1 Prénom1], [Nom2 Prénom2]");

        // Création de la fenêtre de jeu et affichage
        SwingUtilities.invokeLater(() -> {

            List<Monster> availableMonsters = MonsterFactory.createMonsters();
            Deck playerDeck = new Deck(availableMonsters);
            //playerDeck.printDeck();
            Deck enemyDeck = new Deck(availableMonsters);

            GameWindow window = new GameWindow(playerDeck, enemyDeck);
            window.setVisible(true);

            //Le jeu commence avec le tour du joueur
        });
    }
}
