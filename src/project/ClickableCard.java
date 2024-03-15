package project;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickableCard extends JLabel {
    private Card card;
    private ImageIcon originalImage;

    public ClickableCard(ImageIcon originalImage,Card card) {
        this.originalImage = originalImage;

        this.card = card;


        // Utilisez une icône vide pour l'étiquette
        setIcon(originalImage);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCardClick(); // Appel de la méthode pour gérer le clic
            }
        });
    }

    private void handleCardClick() {
        // Vérifier si la carte a un monstre associé
        Monster monster = card.getMonster();
        if (monster != null) {
            // Construire le message avec les détails du monstre
            String message = "Nom : " + monster.getName() + "\n" +
                    "Points de Vie : " + monster.getHealthPoints() + " Points \n" +
                    "Points d'Attaque : " + monster.getAttackPoints() + " Points \n" ;

            // Afficher les détails du monstre dans une boîte de dialogue
            JOptionPane.showMessageDialog(this, message, "Détails du Monstre", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Si la carte n'a pas de monstre associé, afficher un message d'erreur
            JOptionPane.showMessageDialog(this, "Cette carte n'a pas de monstre associé.", "Aucun Monstre", JOptionPane.WARNING_MESSAGE);
        }
    }
}
