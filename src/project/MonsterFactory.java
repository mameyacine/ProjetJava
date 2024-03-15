package project;

import java.util.ArrayList;
import java.util.List;

public class MonsterFactory {

    public static List<Monster> createMonsters() {
        List<Monster> monsters = new ArrayList<>();

        // Création de monstres avec des caractéristiques spécifiques
        monsters.add(new Monster(1, "Guerrier Impitoyable", 120, 40, MonsterType.CLASSIC));
        monsters.add(new Monster(2, "Chimère Flamboyante", 130, 45, MonsterType.CLASSIC));
        monsters.add(new Monster(3, "Phoenix Majestueux", 140, 0, MonsterType.PROTECTOR));
        monsters.add(new Monster(4, "Garde Vaillant", 150, 0, MonsterType.PROTECTOR));
        monsters.add(new Monster(5, "Sentinelle Intrépide", 160, 0, MonsterType.PROTECTOR));
        monsters.add(new Monster(6, "Gardien du Sanctuaire", 170, 0, MonsterType.PROTECTOR));
        monsters.add(new HealerMonster(7, "Soigneur Bienveillant", 80, 0, MonsterType.HEALER, 5 ));
        monsters.add(new HealerMonster(8, "Prêtre Guérisseur", 100, 0, MonsterType.HEALER, 10));
        monsters.add(new Monster(9, "Mascotte Enthousiaste", 100, 0, MonsterType.MASCOT));
        monsters.add(new Monster(10, "Chaton Mignon", 70, 0, MonsterType.MASCOT));

        return monsters;
    }
}
