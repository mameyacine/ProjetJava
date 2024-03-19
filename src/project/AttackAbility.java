package project;

import javax.swing.*;

public class AttackAbility implements SpecialAbility {
    private int damage;
    private GameWindow gameWindow;

    public AttackAbility(int damage, GameWindow gameWindow) {
        this.damage = damage;
        this.gameWindow = gameWindow;
    }

    @Override
    public void useAbility(Entity target) {
        if (target instanceof Champion) {
            Champion opponent = (Champion) target;
            opponent.reduceHealthPoints(damage);
            System.out.println("Attaque spéciale ! " + opponent.getName() + " subit " + damage + " points de dégâts.");
        } else {
            System.out.println("La capacité spéciale ne peut être utilisée que contre un champion.");
        }
    }

    @Override
    public boolean canAttack() {
        return true; // La capacité spéciale permet d'attaquer
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void activate(Champion self, Champion opponent) {
        // Logique spécifique à la capacité d'attaque
        opponent.reduceHealthPoints(damage);
        gameWindow.resetInfo();

        JOptionPane.showMessageDialog(null, "Utilisation de la capacité spéciale : Attaque \n " + opponent.getName() + " subit " + damage + " points de dégâts.\n Points de vie de l'ennemi : " + opponent.getHealthPoints());
    }

}
