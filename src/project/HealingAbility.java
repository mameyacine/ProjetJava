package project;

import javax.swing.*;

public class HealingAbility implements SpecialAbility {
    private int healAmount;
    private GameWindow gameWindow;

    public HealingAbility(int healAmount, GameWindow gameWindow) {
        this.healAmount = healAmount;
        this.gameWindow = gameWindow;
    }

    @Override
    public void useAbility(Entity target) {
        if (target instanceof Champion) {
            Champion self = (Champion) target;
            self.increaseHealthPoints(healAmount);
            System.out.println("Capacité spéciale de guérison activée ! " + self.getName() + " récupère " + healAmount + " points de vie.");
        } else {
            System.out.println("La capacité spéciale ne peut être utilisée que par un champion.");
        }
    }

    @Override
    public boolean canAttack() {
        return true; // La capacité spéciale ne permet pas d'attaquer, mais cette méthode est nécessaire pour l'interface
    }

    public int getHealAmount() {
        return healAmount;
    }

    @Override
    public void activate(Champion self, Champion opponent) {
        // Logique spécifique à la capacité de guérison
        self.increaseHealthPoints(healAmount);
        gameWindow.resetInfo();

        JOptionPane.showMessageDialog(null, "Utilisation de la capacité spéciale : Guérison \n " + self.getName() + " récupère " + healAmount + " points de vie.\n Points de vie du joueur : " + self.getHealthPoints());
    }
}
