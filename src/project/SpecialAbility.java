package project;



public interface SpecialAbility {
    // Méthode pour utiliser la capacité spéciale sur une cible
    void useAbility(Entity target);

    // Méthode pour vérifier si la capacité spéciale permet à un champion d'attaquer
    boolean canAttack();


    // Méthode abstraite à implémenter dans les sous-classes
    public abstract void activate(Champion self, Champion opponent);

}
