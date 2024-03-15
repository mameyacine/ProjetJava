package project;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Monster> summonedMonsters;

    public Board() {
        this.summonedMonsters = new ArrayList<>();
    }



    public void summonMonster(Monster monster) {
        summonedMonsters.add(monster);
        System.out.println("Un monstre a été invoqué sur le plateau : " + monster.getName());
    }







    public List<Monster> getSummonedMonsters() {
        return summonedMonsters;
    }



    // Méthode pour retirer un monstre du plateau
    public void removeMonster(Monster monster) {


        if (summonedMonsters.remove(monster)) {
            if (monster.getType().equals(MonsterType.PROTECTOR)){
                monster.setProtectionTurns(0);
            }
            System.out.println("Le monstre " + monster.getName() + " a été retiré du plateau.");
        } else {
            System.out.println("Le monstre " + monster.getName() + " n'est pas présent sur le plateau.");
        }
    }





    public String printBoard() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Monstres invoqués sur le plateau :\n");
        for (Monster monster : summonedMonsters) {
            stringBuilder.append(monster.getName()).append(" (Type: ").append(monster.getType()).append(", PV: ").append(monster.getHealthPoints()).append(")\n");
        }
        return stringBuilder.toString();
    }


}
