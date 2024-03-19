package project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.ImageIcon;

import org.junit.Before;
import org.junit.Test;

public class Tests {

    private boolean playerTurn;
    private boolean hasAttackedThisTurn;

    private Champion player;
    private Champion activeChampion = player;
    private Champion enemy;
    private GameWindow gameWindow;

    @Before
    public void setUp() {
        playerTurn = true;
        hasAttackedThisTurn = false;
        player = new Champion(1, "Player", 30);
        enemy = new Champion(2, "Enemi", 30);
        gameWindow = new GameWindow(null, null, player.name, enemy.name);
    }


    @Test
    public void testPlayerAttack_TargetMonsterKilled() {
        // Création des monstres simulés
        Monster attacker = new Monster(1, "attacker", 10, 5, MonsterType.CLASSIC);
        Monster target = new Monster(2, "Chaton Mignon", 5, 0, MonsterType.MASCOT);

        attacker.setOwner(player);
        target.setOwner(enemy);

        // Ajout des monstres aux plateaux respectifs
        player.getBoard().summonMonster(attacker);
        enemy.getBoard().summonMonster(target);

        // Simulation de l'attaque du joueur contre le monstre ennemi
        gameWindow.attackEnemyMonster(attacker, target);

        // Vérification que le monstre ennemi a été tué
        assertEquals(0, target.getHealthPoints()); // Vérification que les points de vie du monstre cible ont diminué
        assertTrue(enemy.getBoard().getSummonedMonsters().isEmpty());

    }



    @Test
    public void testCombat_PlayerWins() {
        Monster attackerPlayer = new Monster(1, "attackerPlayer", 5, 30, MonsterType.CLASSIC);

        attackerPlayer.setOwner(player);


        // Initialisation des points de vie des champions
        player.setHealthPoints(30);
        enemy.setHealthPoints(20); // Moins de points de vie pour l'ennemi

        // Simulation du combat
        while (player.getHealthPoints() > 0 && enemy.getHealthPoints() > 0) {
            // Attaque du joueur
                gameWindow.attackEnemy(attackerPlayer, enemy); // Attaque du joueur sur l'ennemi
                System.out.println(enemy.getHealthPoints());


        }

        // Vérification que le joueur a gagné
        assertTrue(player.getHealthPoints() > 0);
        assertTrue(enemy.getHealthPoints() <= 0);
    }


    @Test
    public void testSummonMonster_Successful() {
        Monster monster = new Monster(1, "Dragon", 10, 5, MonsterType.CLASSIC);

        // Invocation du monstre sur le plateau du joueur
        player.getBoard().summonMonster(monster);

        // Vérification que le monstre a été ajouté avec succès
        assertTrue(player.getBoard().getSummonedMonsters().contains(monster));
    }
    @Test
    public void testHealAction() {
        // Création d'un champion avec un nombre initial de points de vie
        Champion champion = new Champion(1, "Healer", 20);

        // Réduction des points de vie du champion (simulation d'un combat ou d'une action)
        champion.reduceHealthPoints(10);

        // Utilisation d'un objet de soin pour récupérer des points de vie
        int healingAmount = 8;
        champion.heal(healingAmount);

        // Vérification que les points de vie ont été correctement récupérés
        assertEquals(18, champion.getHealthPoints()); // Le champion avait 10 points de vie réduits à 2, puis récupérés de 8, donc 18 points de vie au total
    }


    @Test
    public void testBoostAbility() {

        // Création d'un monstre avec une capacité de boost
        Monster booster = new Monster(1, "Booster", 10, 0, MonsterType.MASCOT);
        booster.setOwner(player);

        // Création d'un monstre ennemi
        Monster target = new Monster(2, "Target", 5, 10, MonsterType.CLASSIC);
        target.setOwner(enemy);
        // Ajout des monstres aux plateaux respectifs
        player.getBoard().summonMonster(booster);
        enemy.getBoard().summonMonster(target);

        // Simulation de l'attaque du monstre avec capacité de boost
        gameWindow.boostMonster( target);

        // Vérification que les points de vie du monstre ennemi ont été réduits de 12 (10 dégâts + 2 boost)
        assertEquals(0, target.getAttackPoints());
    }


}

