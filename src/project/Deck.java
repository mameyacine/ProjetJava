
package project;

import java.util.Collections;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;


public class Deck {
    private final List<Card> cards;
    private List<Card> availableCards;


    public Deck(List<Monster> availableMonsters) {
        // Initialiser la liste availableCards avec les cartes disponibles à partir des monstres donnés
        this.availableCards = CardFactory.createCardsFromMonsters(availableMonsters);
        loadCardImages(this.availableCards); // Charger les images des cartes
        this.cards = new ArrayList<>();
        initializeRandomCards(5, new ArrayList<>(this.availableCards)); // Initialiser le deck avec 5 cartes aléatoires
    }






    // Méthode pour charger les images des cartes
    private void loadCardImages(List<Card> cards) {
        for (Card card : cards) {
            try {
                String imagePath = "src/Cards/" + card.getName() + ".png"; // Chemin vers l'image
                System.out.println("Chargement de l'image pour la carte " + card.getName() + "...");

                File file = new File(imagePath);
                Image image = ImageIO.read(file);
                card.setImage(image); // Définir l'image de la carte
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement de l'image pour la carte " + card.getName() + ": " + e.getMessage());
            }
        }
    }



    // Méthode pour piocher une carte du deck
    public Card drawCard() {
        if (!isEmpty()) {
            return cards.remove(0); // Retire et renvoie la première carte du deck
        } else {
            System.out.println("Le deck est vide !");
            return null;
        }
    }

    // Méthode pour initialiser le deck avec des cartes choisies au hasard
    public void initializeRandomCards(int numCards, List<Card> availableCards) {
        Collections.shuffle(availableCards);

        for (int i = 0; i < numCards && i < availableCards.size(); i++) {
            Card card = availableCards.get(i);
            cards.add(card);
        }
    }

    public void createDeck(){
        // Initialisez le deck avec un nombre fixe de cartes choisies au hasard à partir de la liste availableCards
        initializeRandomCards(5, availableCards); // Vous pouvez ajuster le nombre de cartes selon vos besoins

    }


    public void shuffle() {
        Collections.shuffle(cards);
    }








    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }





    public void printDeck() {
        for (Card card : cards) {
            System.out.println( card.getName());
        }
    }





}
