package project;


import java.util.ArrayList;
import java.util.List;

public class Champion extends Entity {
    private Monster selectedMonster; // Le monstre sélectionné par le champion
    private boolean active;

    private SpecialAbility specialAbility;
    private final List<Card> hand;
    private boolean hasUsedSpecialAbilityThisTurn;
    private Champion opponent;
    private Board board;
    private final int maxHealthPoints;

    private Deck deck;
    private boolean isAttacking; // Indique si le champion est en train d'attaquer

    private final List<Monster> monsters; // Liste des monstres associés au champion

    // Constructeur
    public Champion(int id, String name, int healthPoints) {
        super(id, name, healthPoints);
        this.specialAbility = null; // Capacité spéciale par défaut
        this.maxHealthPoints = 100; // Valeur par défaut pour les points de vie maximum
        this.hasUsedSpecialAbilityThisTurn = false;
        this.board = new Board();
        this.monsters = new ArrayList<>();

        this.hand = new ArrayList<>();

        // Par défaut, définissez le champion comme actif lorsqu'il est créé
        active = true;
    }








    public void reduceHealthPoints(int amount) {
        healthPoints -= amount;
        // Assurez-vous que les points de vie ne deviennent pas négatifs
        if (healthPoints < 0) {
            healthPoints = 0;
        }
    }

    // Méthode pour obtenir le deck du joueur

    public Board getBoard() {
        return this.board;
    }
    public Board setBoard(Board board) {
        this.board = board;
        return this.board;
    }


    // Créer une méthode pour définir la capacité spéciale du champion
    public void setSpecialAbility(SpecialAbility specialAbility) {
        this.specialAbility = specialAbility;
    }



    // Méthode pour obtenir la main du joueur
    public List<Card> getHand() {
        return hand;
    }

    // Getter et setter pour specialAbility
    public SpecialAbility getSpecialAbility() {
        return specialAbility;
    }


    public boolean canAttack() {
        return specialAbility != null && specialAbility.canAttack();
    }


    // Méthode pour attaquer une cible
    public void attack(Entity target) {
        // Vérifiez si le champion peut attaquer (par exemple, s'il possède une arme équipée)
        if (canAttack()) {
            // Calculez les dégâts infligés par le champion
            int damage = getAttackDamage();

            // Infligez les dégâts à la cible
            target.takeDamage(damage);

            // Affichez un message indiquant les détails de l'attaque
            System.out.println(getName() + " attaque " + target.getName() + " et lui inflige " + damage + " points de dégâts.");
        } else {
            System.out.println(getName() + " ne peut pas attaquer pour le moment.");
        }
    }

    // Méthode pour obtenir les dégâts d'attaque du champion
    public int getAttackDamage() {
        int baseDamage = 0;

        // Vérifier si le champion peut attaquer en fonction de sa capacité spéciale
        if (canAttack()) {
            // Remplacer getBaseAttackDamage() par le montant de dégâts d'attaque que vous souhaitez attribuer
            baseDamage += 5; // Exemple de dégâts d'attaque de base
        }

        return baseDamage;
    }



    // Méthode pour définir l'état d'attaque du champion
    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }



    // Méthode pour ajouter une carte à la main du champion
    public void addToHand(Card card) {
        hand.add(card);
        System.out.println("Carte " + card.getName() + " ajoutée à la main !");
    }

    public void removeCardFromHand(Card card) {
        hand.remove(card);
        System.out.println("Carte " + card.getName() + " retirée de la main !");
    }



    // Méthode pour guérir le champion
    public void heal(int amount) {
        int currentHealth = getHealthPoints();
        int newHealth = currentHealth + amount;
        setHealthPoints(newHealth);
    }




    // Méthode pour obtenir les monstres soigneurs du champion sur le plateau
    public List<HealerMonster> getHealers() {
        List<HealerMonster> healers = new ArrayList<>();
        List<Monster> summonedMonsters = board.getSummonedMonsters();
        for (Monster monster : summonedMonsters) {
            if (monster instanceof HealerMonster) {
                healers.add((HealerMonster) monster);
            }
        }
        return healers;
    }

    // Méthode pour obtenir les monstres mascottes du champion sur le plateau
    public List<Monster> getMascots() {
        List<Monster> mascots = new ArrayList<>();
        List<Monster> summonedMonsters = board.getSummonedMonsters();
        for (Monster monster : summonedMonsters) {
            if (monster.getType() == MonsterType.MASCOT) {
                mascots.add(monster);
            }
        }
        return mascots;
    }




}
