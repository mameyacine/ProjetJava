package project;


// Classe représentant un monstre protecteur
class ProtectorMonster extends Monster {
    private boolean hasTaunt;

    public ProtectorMonster(int id, String name, int healthPoints, int attackPoints, MonsterType type, boolean hasTaunt) {
        super(id, name, healthPoints, attackPoints, type);
        this.hasTaunt = hasTaunt;
    }

    public boolean hasTaunt() {
        return hasTaunt;
    }

    public void setTaunt(boolean hasTaunt) {
        this.hasTaunt = hasTaunt;
    }
}

