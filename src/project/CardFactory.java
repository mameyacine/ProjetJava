package project;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CardFactory {

    public static List<Card> createCardsFromMonsters(List<Monster> monsters) {
        List<Card> cards = new ArrayList<>();

        for (Monster monster : monsters) {
            int id = monster.getId();
            String name = monster.getName();

            Card card = new Card(id, name , monster);

            try {
                String imageName = card.getName() ;
                //System.out.println(imageName);// Nom de l'image
                String imagePath = "src/Cards/" + imageName + ".png"; // Chemin vers l'image
                File file = new File(imagePath);
                Image image = ImageIO.read(file);
                card.setImage(image); // Définir l'image de la carte
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement de l'image cf: " + e.getMessage());
            }

            cards.add(card);
        }

        return cards;
    }
}
