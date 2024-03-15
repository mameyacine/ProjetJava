
package project;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Card {
    private int id;
    private String name;
    private Champion champion;

    private Monster monster;
    private Image image;



    public Card(int id, String name, Monster monster) {
        this.id = id;
        this.name = name;
        this.monster = monster;
        try {
            //System.out.println(name);
            String imagePath = "src/Cards/" + name + ".png";
            File file = new File(imagePath);
            this.image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de l'image c : " + e.getMessage());
        }
    }




    public String getName() {
        return name;
    }


    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    // Méthode pour utiliser la carte et invoquer le monstre associé sur le plateau du champion
    public void useCard(Champion champion) {
        // Vérifier si la carte est associée à un monstre
        if (monster != null) {
            // Récupérer le plateau du champion
            Board board = champion.getBoard();
            if (board != null) {
                // Invoquer le monstre sur le plateau
                board.summonMonster(monster);
                // Retirer la carte de la main du champion
                champion.removeCardFromHand(this);
            } else {
                System.out.println("Le champion n'a pas de plateau pour invoquer le monstre.");
            }
        } else {
            System.out.println("Cette carte n'est pas associée à un monstre.");
        }
    }












}

