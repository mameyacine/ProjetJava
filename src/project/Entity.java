package project;

public abstract class Entity {
    private int id;
    public String name;
    public int healthPoints;

    public Entity(int id, String name, int healthPoints) {
        this.id = id;
        this.name = name;
        this.healthPoints = healthPoints;
    }

    public void reduceHealthPoints(int damage) {
        healthPoints -= damage;
        if (healthPoints < 0) {
            healthPoints = 0;
        }
    }

    // Getters et setters
    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }


    // Méthode pour attaquer une entité
    public abstract void attack(Entity target);

    // Méthode pour réduire les points de vie après une attaque
    public void takeDamage(int damage) {
        int currentHealth = getHealthPoints();
        setHealthPoints(currentHealth - damage);
        if (currentHealth - damage <= 0) {
            System.out.println(getName() + " a été vaincu !");
        }
    }


}

