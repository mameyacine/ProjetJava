package project;




// Classe représentant un monstre protecteur
class HealerMonster extends Monster {
    private int healAmount;

    public HealerMonster(int id, String name, int healthPoints, int attackPoints, MonsterType type, int healAmount) {
        super(id, name, healthPoints, attackPoints, type);
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int healAmount) {
        this.healAmount = healAmount;
    }
}


