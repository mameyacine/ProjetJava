package project;

import java.util.Random;

public class AbilityFactory {

    private static final Random random = new Random();

    public static SpecialAbility createRandomAbility(GameWindow gameWindow) {
        int randomNum = random.nextInt(2); // Generate a random number between 0 and 1

        // Return a random ability based on the random number
        if (randomNum == 0) {
            return new AttackAbility(20, gameWindow);
        } else {
            return new HealingAbility(20, gameWindow);
        }
    }
}
