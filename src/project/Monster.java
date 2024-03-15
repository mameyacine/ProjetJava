package project;



import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class Monster extends Entity {
    private int protectionTurns; // Nombre de tours restants avec la capacité de protection

    private int attackPoints;
    private MonsterType type;
    private Image image;
    private Champion owner;
    private static Map<MonsterType, List<Monster>> protectors = new HashMap<>();



    private boolean hasAttacked;


    // Constructeur
    public Monster(int id, String name, int healthPoints, int attackPoints, MonsterType type) {
        super(id, name, healthPoints);
        this.attackPoints = attackPoints;
        this.type = type;
        this.image = loadImage(name);
        this.owner = null;
        this.protectionTurns = 0;



    }

    public TargetType getTargetType() {
        // Déterminer le type de cible en fonction du type de monstre protecteur
        if (this.type == MonsterType.PROTECTOR) {
            if (this.getName().equals("Phoenix Majestueux")|| this.getName().equals("Garde Vaillant")) {
                return TargetType.CHAMPION; // Le Protector1 cible le champion
            } else {
                return TargetType.NON_CHAMPION; // Par défaut, les autres protecteurs ciblent les autres monstres
            }
        } else {
            return TargetType.NON_CHAMPION; // Par défaut, les autres monstres ciblent les non-champions
        }
    }



    // Méthode pour invoquer le monstre protecteur

    // Méthode pour invoquer le monstre protecteur
    public void invokeProtector() {
        if (this.type == MonsterType.PROTECTOR) {
            List<Monster> protectorsOfType = protectors.getOrDefault(this.type, new ArrayList<>());
            // Si un monstre protecteur du même type existe déjà, retirez-le de la liste
            if (!protectorsOfType.isEmpty()) {
                Monster firstProtector = protectorsOfType.remove(0);
                firstProtector.resetProtectionTurns(); // Réinitialiser la protection du premier monstre invoqué
            }
            protectorsOfType.add(this);
            protectors.put(this.type, protectorsOfType);
            // Initialiser la durée de la protection en fonction du type de cible
            if (this.getTargetType() == TargetType.CHAMPION) {
                this.protectionTurns = 4;
                System.out.println("La protection de " + this.getName() + " qui protège le champion est maintenant active pendant " + this.protectionTurns + " tours.");
            } else {
                this.protectionTurns = 8;
                System.out.println("La protection de " + this.getName() + " est maintenant active pendant " + this.protectionTurns + " tours.");
            }
        }
    }

    // Méthode pour réinitialiser les tours de protection
    private void resetProtectionTurns() {
        this.protectionTurns = 0;
    }

    // Méthode pour réduire le nombre de tours restants avec la protection pour tous les monstres protecteurs invoqués
    public static void reduceProtectionTurnsForAll() {
        for (List<Monster> protectorsOfType : protectors.values()) {
            for (Monster protector : protectorsOfType) {
                protector.reduceProtectionTurns();
            }
        }
    }

    // Méthode pour réduire le nombre de tours restants avec la protection
    public void reduceProtectionTurns() {
        if (this.protectionTurns > 0) {
            System.out.println(this.getTargetType());
            this.protectionTurns--;
            setProtectionTurns(this.protectionTurns);
            System.out.println("La protection de " + this.getName() + " dont la cible de protection est "+ this.getTargetType() + " est maintenant active pendant " + this.protectionTurns + " tours.");
            if (this.protectionTurns <= 0) {
                setProtectionTurns(0);
                // La protection expire après le nombre de tours spécifié
                System.out.println("La protection de " + this.getName() + " expire.");
                JOptionPane.showMessageDialog(null, "La protection de " + this.getName() + " a expiré.");
            }
        }
    }


    public void heal(int amount) {
        int currentHealth = getHealthPoints();
        int newHealth = currentHealth + amount;
        setHealthPoints(newHealth);
    }

    public boolean isProtectionActive() {
        System.out.println(protectionTurns);
        return protectionTurns > 0;
    }




    @Override
    public void attack(Entity entity) {
        if (entity instanceof Monster) {
            Monster target = (Monster) entity;
            // Réduisez les points de vie du monstre ciblé en fonction des points d'attaque de ce monstre
            target.reduceHealthPoints(attackPoints);
            // Réduisez les points de vie de ce monstre en fonction des points d'attaque du monstre ciblé
            reduceHealthPoints(target.getAttackPoints());
        } else if (entity instanceof Champion) {
            Champion target = (Champion) entity;
            // Réduisez les points de vie du champion ciblé en fonction des points d'attaque de ce monstre
            target.reduceHealthPoints(attackPoints);
        }
    }

    public void setProtectionTurns(int protectionTurns) {
        this.protectionTurns = protectionTurns;
    }




    private Image loadImage(String name) {
        try {
            String imagePath = "src/Monsters/" + name + ".png";
            File file = new File(imagePath);
            return ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de l'image du monstre " + name + ": " + e.getMessage());
            return null;
        }
    }



    public Champion getOwner() {
        return owner;
    }

    public void setOwner(Champion owner) {
        this.owner = owner;
    }


    // Méthode pour obtenir l'image du monstre
    public Image getImage() {
        return image;
    }

    public boolean hasAttacked() {
        return hasAttacked;
    }

    // Méthode pour vérifier si le monstre peut attaquer
    public boolean canAttack() {
        return !hasAttacked; // Le monstre peut attaquer s'il n'a pas encore attaqué dans ce tour
    }








    // Méthode pour définir l'état d'attaque du monstre
    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }


    // Autres membres de la classe Monster

    // Méthode pour réduire les points de vie du monstre
    public void reduceHealthPoints(int amount) {
        healthPoints -= amount;
        // Assurez-vous que les points de vie ne deviennent pas négatifs
        if (healthPoints < 0) {
            healthPoints = 0;
        }
    }


    // Getter et setter pour attackPoints
    public int getAttackPoints() {
        return attackPoints;
    }

    public int getProtectionTurns() {
        return protectionTurns;
    }


    public void setAttackPoints(int attackPoints) {
        this.attackPoints = attackPoints;
    }

    // Getter et setter pour type
    public MonsterType getType() {
        return type;
    }

    public void setType(MonsterType type) {
        this.type = type;
    }


}
