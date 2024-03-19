package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class GameWindow extends JFrame {
    private static final Logger logger = Logger.getLogger(GameWindow.class.getName());
    private static FileHandler fileHandler;

    private int specialAbilityUsageCount = 0;
    private int enemySpecialAbilityCount = 0;

    private static ClickableMonster activeMonster;

    private boolean hasDrawnCardThisTurn = false;
    private boolean hasAttackedThisTurn = false;
    private boolean hasUsedSpecialAbilityThisTurn = false;

    private ClickableMonster selectedPlayerMonster;
    private JLabel enemyIconLabel;

    private ClickableMonster attackingMonster;
    private ClickableMonster targetMonster;
    private ClickableMonster healedMonster;

    private ClickableMonster selectedEnemyMonster;
    private ClickableMonster selectedHealer;
    private JLabel playerHealthLabel;
    private JLabel InfoLabel;

    private JLabel enemyHealthLabel;
    private JButton playerAttackButton;
    private JButton playerEndTurnButton;
    private JLabel currentPlayerLabel;
    private boolean isActive = false;

    private Champion player;
    private Champion enemy;
    private Deck playerDeck;
    private Deck enemyDeck;
    private List<Card> playerHand;
    private List<Card> enemyHand;

    private JPanel playerHandPanel;
    private JPanel enemyHandPanel;

    private boolean playerTurn = true;
    private JPanel playerBoardPanel;
    private JPanel enemyBoardPanel;

    static {
        try {
            // Set up the log file
            fileHandler = new FileHandler("game.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameWindow(Deck playerDeck, Deck enemyDeck, String playerName, String enemyName) {
        this.playerDeck = playerDeck;
        this.enemyDeck = enemyDeck;



        // Demander le nom de l'utilisateur

        // Vérifier si l'utilisateur a saisi un nom valide
        if (playerName != null && !playerName.isEmpty()) {
            // Initialiser le champion du joueur avec le nom saisi
            player = new Champion(1, playerName, 30);
            enemy = new Champion(2, enemyName, 30); // Ennemi générique

            playerHand = player.getHand();
            enemyHand = enemy.getHand();

            player.setSpecialAbility(AbilityFactory.createRandomAbility(this));
            enemy.setSpecialAbility(AbilityFactory.createRandomAbility(this));


            // Configurer la fenêtre
            setTitle("Hearthstone");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(900, 700);
            setLocationRelativeTo(null);

            // Créer le panneau principal comme un BackgroundPanel pour afficher l'image de fond
            BackgroundPanel mainPanel = new BackgroundPanel();
            mainPanel.setLayout(new GridLayout(2, 1));

            // Créez une bordure avec un titre personnalisé et une couleur de texte blanche
            TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Ennemi", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.BOLD, 14), Color.WHITE);
            titledBorder.setTitleColor(Color.WHITE);

            // Créez un panneau pour représenter l'ennemi et appliquez la bordure
            JPanel enemyPanel = new JPanel(new BorderLayout());
            enemyPanel.setBorder(titledBorder);


            // Chargement de l'image de l'ennemi depuis un fichier
            ImageIcon enemyIcon = new ImageIcon("src/Icons/enemy.jpeg");

            // Obtenir l'image de l'icône
            Image image = enemyIcon.getImage();

            // Redimensionner l'image tout en conservant ses proportions d'origine
            Image scaledImage = image.getScaledInstance(60, -1, Image.SCALE_SMOOTH);

            // Création d'un JLabel pour afficher l'image de l'ennemi
            JLabel enemyIconLabel = new JLabel(new ImageIcon(scaledImage));
            enemyIconLabel.setPreferredSize(new Dimension(60, 60));

            // Création d'un JPanel pour contenir l'image de l'ennemi
            JPanel enemyIconPanel = new JPanel();
            enemyIconPanel.add(enemyIconLabel);
            enemyIconPanel.setPreferredSize(new Dimension(scaledImage.getWidth(null), scaledImage.getHeight(null)));

            // Ajout d'un écouteur de clics de souris sur l'image
            enemyIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!isActive) {
                        isActive = true;
                        // Mettre à jour l'apparence de l'image pour indiquer qu'elle est sélectionnée
                        enemyIconLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    } else {
                        isActive = false;
                        // Rétablir l'apparence normale de l'image
                        enemyIconLabel.setBorder(null);
                    }
                }
            });

            // Ajout de l'étiquette à votre interface utilisateur
            enemyPanel.add(enemyIconLabel, BorderLayout.NORTH);

            // Créez un panneau pour la main de l'ennemi
            enemyHandPanel = new JPanel(new FlowLayout());


            // Créer un bouton pour le plateau de l'ennemi
            enemyBoardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            // Ajouter le panneau du plateau du joueur à un panneau pour le positionnement
            JPanel boardContainerPanel1 = new JPanel(new BorderLayout());
            boardContainerPanel1.add(enemyBoardPanel, BorderLayout.CENTER);
            enemyPanel.add(boardContainerPanel1, BorderLayout.SOUTH);

            //partie joueur

            // Créez une bordure avec un titre personnalisé et une couleur de texte blanche
            TitledBorder titledBorderPlayer = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), playerName, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.BOLD, 14), Color.WHITE);
            titledBorderPlayer.setTitleColor(Color.WHITE);

            // Créez un panneau pour représenter le joueur et appliquez la bordure
            JPanel playerPanel = new JPanel(new BorderLayout());
            playerPanel.setBorder(titledBorderPlayer);


            currentPlayerLabel = new JLabel("Tour du joueur");
            playerPanel.add(currentPlayerLabel, BorderLayout.SOUTH);

            // Initialisation des composants pour l'ennemi
            enemyHealthLabel = new JLabel("Points de vie de l'ennemi : " + enemy.getHealthPoints());


            playerHealthLabel = new JLabel("Points de vie du joueur: " + player.getHealthPoints());

            // Créez le panneau pour représenter le plateau du joueur
            playerBoardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            //playerBoardPanel.setBackground(Color.LIGHT_GRAY);

            // Ajouter le panneau du plateau du joueur à un panneau pour le positionnement
            JPanel boardContainerPanel2 = new JPanel(new BorderLayout());
            boardContainerPanel2.add(playerBoardPanel, BorderLayout.CENTER);

            // Ajouter le panneau du plateau du joueur au panneau principal du joueur (en haut)
            playerPanel.add(boardContainerPanel2, BorderLayout.NORTH);

            // Ajouter les boutons du joueur au panneau principal du joueur (au milieu)
            JPanel playerButtonPanel = new JPanel(new FlowLayout());
            JButton drawButton = new JButton("Piocher une carte");
            drawButton.addActionListener(e -> {
                drawCard(playerDeck);
                updatePlayerHand();
            });
            playerButtonPanel.add(drawButton);

            JButton playCardButton = new JButton("Jouer une carte");
            playCardButton.addActionListener(e -> chooseCardToPlay());
            playerButtonPanel.add(playCardButton);

            playerAttackButton = new JButton("Attaquer");
            playerAttackButton.addActionListener(e -> playerAttack());
            playerButtonPanel.add(playerAttackButton);

            JButton specialAbilityButton = new JButton("Capacité spéciale");
            specialAbilityButton.addActionListener(e -> useSpecialAbility());
            playerButtonPanel.add(specialAbilityButton);

            JButton healButton = new JButton("Soin");
            healButton.addActionListener(e -> useHeal());
            playerButtonPanel.add(healButton);

            JButton mascotButton = new JButton("Boost");
            mascotButton.addActionListener(e -> useMascot());
            playerButtonPanel.add(mascotButton);

            playerEndTurnButton = new JButton("Fin de tour");
            playerEndTurnButton.addActionListener(e -> playerEndTurn());
            playerButtonPanel.add(playerEndTurnButton);

            playerPanel.add(playerButtonPanel, BorderLayout.CENTER);

            // Ajouter la main du joueur au panneau principal du joueur (en bas)
            playerHandPanel = new JPanel(new FlowLayout());
            playerPanel.add(playerHandPanel, BorderLayout.SOUTH);

            // Création du label pour afficher les informations du monstre
            InfoLabel = new JLabel("Ennemi: " + enemy.getHealthPoints() + "PV, Joueur : " + player.getHealthPoints() + "PV");
            InfoLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrage du texte
            add(InfoLabel, BorderLayout.SOUTH); // Ajout du label en bas de la fenêtre
            // Pour les panneaux principaux
            mainPanel.setOpaque(false);
            enemyPanel.setOpaque(false);
            playerPanel.setOpaque(false);
            playerHandPanel.setOpaque(false);
            playerButtonPanel.setOpaque(false);
            playerBoardPanel.setOpaque(false);
            boardContainerPanel2.setOpaque(false);
            enemyBoardPanel.setOpaque(false);
            boardContainerPanel1.setOpaque(false);


            currentPlayerLabel.setOpaque(false);


            // Ajouter les panneaux du joueur et de l'ennemi au panneau principal
            mainPanel.add(enemyPanel);
            mainPanel.add(playerPanel);

            // Ajouter le panneau principal au contenu de la fenêtre
            add(mainPanel);
        }
    }

    private void playCard(Card card) {
        // Récupérer le champion actif qui joue la carte
        Champion activeChampion = getCurrentActiveChampion();
        System.out.println("Le champion actif est : " + activeChampion.getName());

        // Vérifier si la carte est de type "Protector"
        if (card.getMonster() != null && card.getMonster().getType() == MonsterType.PROTECTOR) {

            // Si le type est "Protector", invoquer la protection
            card.getMonster().invokeProtector();
            logger.info(activeChampion.getName() + " a joué un Protector, La protection a été invoquée avec succès!");
            logger.info("Le Protector est de type : " + card.getMonster().getTargetType());

            System.out.println(card.getMonster().getTargetType());
            JOptionPane.showMessageDialog(this, "Le Protector a été invoqué avec succès!");
        }

        // Ajouter l'icône du monstre invoqué au plateau du joueur si le champion actif est le joueur
        if (activeChampion.equals(player) && card.getMonster() != null) {
            card.useCard(player);
            updateHandAfterPlayingCard(card);

            Monster monster = card.getMonster();
            monster.setOwner(player); // Attribuer le joueur actif comme propriétaire du monstre

            Image img = monster.getImage();
            Image newImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(newImg);
            ClickableMonster clickableMonster = new ClickableMonster(scaledIcon, monster, player, enemy);

            // Créer un JPanel pour contenir le monstre
            JPanel monsterPanel = new JPanel();
            monsterPanel.add(clickableMonster);
            logger.info("Le joueur a invoqué un monstre : " + monster.getName());

            // Ajouter le JPanel contenant le monstre au plateau du joueur
            playerBoardPanel.add(monsterPanel);

            // Rafraîchir le panneau du plateau du joueur pour refléter les modifications
            playerBoardPanel.revalidate();
            playerBoardPanel.repaint();
        } else if (activeChampion.equals(enemy) && card.getMonster() != null) {
            card.useCard(enemy);
            updateEnemyHandAfterPlayingCard(card);

            Monster monster = card.getMonster();
            monster.setOwner(enemy); // Attribuer l'ennemi actif comme propriétaire du monstre

            Image img = monster.getImage();
            Image newImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(newImg);
            ClickableMonster clickableMonster = new ClickableMonster(scaledIcon, monster, player, enemy);

            // Créer un JPanel pour contenir le monstre
            JPanel monsterPanel = new JPanel();
            monsterPanel.add(clickableMonster);
            logger.info("L'ennemi a invoqué un monstre : " + monster.getName());


            // Ajouter le JPanel contenant le monstre au plateau de l'ennemi
            enemyBoardPanel.add(monsterPanel);

            // Rafraîchir le panneau du plateau de l'ennemi pour refléter les modifications
            enemyBoardPanel.revalidate();
            enemyBoardPanel.repaint();
        }

        // Rafraîchir les panneaux de la main des joueurs pour refléter les modifications
        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }

    private void playerEndTurn() {
        logger.info("Le joueur a fini de jouer son tour");
        playerTurn = false;
        hasDrawnCardThisTurn = false;
        hasAttackedThisTurn = false;
        hasUsedSpecialAbilityThisTurn = false;
        currentPlayerLabel.setText("Tour de l'ennemi");
        // Réduire le nombre de tours restants pour les monstres protecteurs sur le plateau du joueur
        reduceProtectionTurns(enemy.getBoard().getSummonedMonsters());

        enemyTurn();


        //verifier si le partie est terminée
        endGame();


    }


    private void drawEnemyCard(Deck deck) {
        if (!deck.isEmpty()) {
            Card card = deck.drawCard();
            if (card != null) {
                enemy.addToHand(card);
                logger.info("L'ennemi a piocé une carte : " + card.getName());
                updateEnemyHand(); // Mettre à jour l'affichage de la main du joueur
                logger.info("Mise à jour de la main de l'ennemi");
            } else {
                System.out.println("Card is null");
                logger.info("Carte nulle");
            }
        } else {
            deck.createDeck(); // Créer un nouveau deck

            System.out.println("Le deck est vide ! Création d'un nouveau deck. L'ennemi perd 5 points de vie.");
            logger.info("Le deck est vide ! Création d'un nouveau deck. L'ennemi perd 5 points de vie.");
            enemy.reduceHealthPoints(5);
            drawEnemyCard(deck); // Piocher une carte du nouveau deck

        }
    }

    private void playRandomCardFromHand() {
        List<Card> enemyHand = enemy.getHand();
        if (!enemyHand.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(enemyHand.size());
            Card enemyCard = enemyHand.get(randomIndex);
            playCard(enemyCard);
            logger.info("L'ennemi a joué une carte : " + enemyCard.getName());
            JOptionPane.showMessageDialog(this, "L'ennemi a joué une carte : " + enemyCard.getName());
        }
    }

    private void enemyTurn() {
        if (!playerTurn) {
            // Faites piocher une carte à l'ennemi
            drawEnemyCard(enemyDeck);


            Random random = new Random();
            double playCardProbability = random.nextDouble();
            System.out.println("Probabilité de jouer une carte : " + playCardProbability);

            // Jouer une carte avec une certaine probabilité
            if (playCardProbability < 0.8) {
                playRandomCardFromHand();
            } else {
                System.out.println("L'ennemi ne joue pas de carte.");
            }

            double attackProbability = random.nextDouble();
            System.out.println("Probabilité d'attaquer : " + attackProbability);

            if (attackProbability < 0.5) {

                double attackMonsterProbability = random.nextDouble();
                System.out.println("Probabilité d'attaquer un monstre : " + attackMonsterProbability);
                if (attackMonsterProbability < 0.5) {
                    attackMonster();
                    System.out.println("L'ennemi a décidé d'attaquer un monstre ce tour.");


                } else {
                    double attackPlayerProbability = random.nextDouble();
                    System.out.println("Probabilité d'attaquer le joueur : " + attackPlayerProbability);
                    if (attackPlayerProbability < 0.3) {
                        System.out.println("L'ennemi a décidé d'attaquer le joueur ce tour.");
                        attackPlayer();
                        endGame();
                    }
                }
            }


            useEnemySpecialAbility();


            double boostProbability = random.nextDouble();
            System.out.println("Probabilité de booster un monstre : " + boostProbability);


            if (boostProbability < 0.3) {
                boostWithRandomMascot();
            } else {
                System.out.println("L'ennemi ne booste pas.");
            }


            heal();


            // Réduire le nombre de tours restants pour les monstres protecteurs sur le plateau de l'ennemi
            reduceProtectionTurns(player.getBoard().getSummonedMonsters());

            // Réactivez le tour du joueur après le tour de l'ennemi
            playerTurn = true;
            System.out.println("Le tour de l'ennemi est terminé.");
            JOptionPane.showMessageDialog(this, "Le tour de l'ennemi est terminé. C'est à vous de jouer");
            currentPlayerLabel.setText("Tour du joueur");
        }
    }

    private void heal() {

        // Récupérer les monstres soigneurs de l'ennemi
        List<HealerMonster> healers = enemy.getHealers();

        // Si aucun soigneur n'est disponible, afficher un message et retourner
        if (healers.isEmpty()) {
            System.out.println("Aucun monstre soigneur ennemi disponible.");
            return;
        }

        // Générer une probabilité pour décider si l'ennemi utilise le soin
        Random random = new Random();
        double useHealProbability = random.nextDouble();
        System.out.println("Probabilité d'utiliser le soin : " + useHealProbability);

        // Si la probabilité est inférieure à 0.5, l'ennemi utilise le soin
        if (useHealProbability < 0.35) {
            // Sélectionner un monstre soigneur de l'ennemi au hasard
            int randomIndex = random.nextInt(healers.size());
            HealerMonster selectedHealer = healers.get(randomIndex);

            // Sélectionner aléatoirement la cible du soin
            String[] options = {"Joueur", "Ennemi", "Monstre"};
            int choice = random.nextInt(options.length);
            System.out.println("L'ennemi a choisi de soigner : " + options[choice]);

            // Traiter la sélection de la cible du soin
            switch (choice) {
                case 0: // Se soigner
                    enemy.heal(selectedHealer.getHealAmount());
                    selectedHealer.setHealthPoints(selectedHealer.getHealthPoints() - selectedHealer.getHealAmount());
                    System.out.println("L'ennemi se soigne de  " + selectedHealer.getHealAmount() + " PV.");
                    logger.info("Ennemi se soigne");
                    break;
                case 1: // Soigner le joueur
                    player.heal(selectedHealer.getHealAmount());
                    selectedHealer.setHealthPoints(selectedHealer.getHealthPoints() - selectedHealer.getHealAmount());
                    System.out.println("L'ennemi soigne le joueur de " + selectedHealer.getHealAmount() + " PV.");
                    logger.info("Ennemi soigne Joueur");
                    break;
                case 2: // Soigner un monstre
                    List<Monster> remainingMonsters = new ArrayList<>(enemy.getBoard().getSummonedMonsters());
                    remainingMonsters.remove(selectedHealer); // Retirer le soigneur de la liste

                    if (!remainingMonsters.isEmpty()) {
                        int randomMonsterIndex = random.nextInt(remainingMonsters.size());
                        Monster selectedTargetMonster = remainingMonsters.get(randomMonsterIndex);
                        selectedTargetMonster.heal(selectedHealer.getHealAmount());
                        selectedHealer.setHealthPoints(selectedHealer.getHealthPoints() - selectedHealer.getHealAmount());
                        System.out.println("L'ennemi a soigné le monstre " + selectedTargetMonster.getName() + " de " + selectedHealer.getHealAmount() + " PV.");
                        logger.info("Ennemi soigne Monstre");
                    } else {
                        System.out.println("Aucun monstre à soigner ennemi disponible.");
                    }
                    break;
                default:
                    System.out.println("Opération annulée.");
                    break;
            }
        } else {
            System.out.println("L'ennemi ne soigne pas ce tour.");
        }
    }


    public void attackMonster() {
        List<Monster> enemyMonsters = enemy.getBoard().getSummonedMonsters();
        List<Monster> playerMonsters = player.getBoard().getSummonedMonsters();

        // Vérifiez si l'ennemi a des monstres à attaquer et si le joueur a des monstres sur le plateau
        if (!enemyMonsters.isEmpty() && !playerMonsters.isEmpty()) {
            Random random = new Random();
            // Choisissez un monstre ennemi de type classique au hasard
            List<Monster> classicEnemyMonsters = enemyMonsters.stream()
                    .filter(monster -> monster.getType().equals(MonsterType.CLASSIC))
                    .collect(Collectors.toList());

            if (!classicEnemyMonsters.isEmpty()) {
                int randomEnemyIndex = random.nextInt(classicEnemyMonsters.size());
                Monster attackingMonster = classicEnemyMonsters.get(randomEnemyIndex);
                System.out.println("L'ennemi choisit le monstre classique : " + attackingMonster.getName());

                // Vérifier si un monstre protecteur ennemi est actif
                boolean protectorActive = isProtectionActive(player.getBoard().getSummonedMonsters());

                if (protectorActive) {
                    // Attaquer la protection ennemie
                    for (Monster monster : player.getBoard().getSummonedMonsters()) {
                        if (monster.getType() == MonsterType.PROTECTOR && monster.isProtectionActive()) {
                            attackEnemyMonster(attackingMonster, monster);
                            logger.info("Ennemi attaque Protecteur");

                            if (monster.getHealthPoints() <= 0) {
                                System.out.println("Le monstre protecteur a été tué !");
                                logger.info("Monstre Protecteur Tué");
                                player.getBoard().removeMonster(monster);
                                removeMonsterFromPanel(playerBoardPanel, monster); // Supprimer le monstre du panneau du joueur
                            }
                            System.out.println("L'ennemi attaque le monstre protecteur : " + monster.getName());
                            JOptionPane.showMessageDialog(this, "L'ennemi attaque le monstre protecteur : " + monster.getName());
                            return;
                        }
                    }
                } else {
                    // Choisissez un monstre joueur au hasard pour l'attaque
                    int randomPlayerIndex = random.nextInt(playerMonsters.size());
                    Monster targetMonster = playerMonsters.get(randomPlayerIndex);
                    System.out.println("L'ennemi choisit la cible : " + targetMonster.getName());

                    // Effectuer l'attaque
                    attackEnemyMonster(attackingMonster, targetMonster);
                    logger.info("Ennemi: " + attackingMonster.getName() + " attaque " + targetMonster.getName());
                    System.out.println("L'ennemi attaque le monstre joueur : " + targetMonster.getName());
                    JOptionPane.showMessageDialog(this, "L'ennemi attaque le monstre joueur : " + targetMonster.getName());

                    // Vérifier si le monstre attaqué est mort
                    if (targetMonster.getHealthPoints() <= 0) {
                        System.out.println("Le monstre joueur a été tué !");
                        logger.info(targetMonster.getName() + " Tué");
                        player.getBoard().removeMonster(targetMonster);
                        removeMonsterFromPanel(playerBoardPanel, targetMonster); // Supprimer le monstre du panneau du joueur
                    }
                }
            } else {
                System.out.println("L'ennemi n'a aucun monstre classique à attaquer.");
            }
        } else {
            System.out.println("L'ennemi ou le joueur n'a aucun monstre sur le plateau.");
        }
    }


    private void attackPlayer() {
        List<Monster> enemyMonsters = enemy.getBoard().getSummonedMonsters();
        List<Monster> classicEnemyMonsters = enemyMonsters.stream()
                .filter(monster -> monster.getType().equals(MonsterType.CLASSIC))
                .collect(Collectors.toList());

        if (!classicEnemyMonsters.isEmpty()) {
            Random random = new Random();
            int randomEnemyIndex = random.nextInt(classicEnemyMonsters.size());
            Monster attackingMonster = classicEnemyMonsters.get(randomEnemyIndex);
            System.out.println("L'ennemi choisit le monstre classique : " + attackingMonster.getName());

            // Vérifier si un monstre protecteur joueur est actif
            boolean protectorActive = isProtectionActive(enemyMonsters);
            System.out.println("Protector active? " + protectorActive);
            if (protectorActive) {
                for (Monster monster : player.getBoard().getSummonedMonsters()) {
                    if (monster.getType() == MonsterType.PROTECTOR && monster.isProtectionActive() && monster.getTargetType() == TargetType.CHAMPION) {
                        JOptionPane.showMessageDialog(this, "Vous ne pouvez pas attaquer, un monstre " + monster.getName() + " protecteur ennemi est actif ! Vous devez d'abord le tuer !");
                        return;
                    }
                }
            }

            // Effectuer les actions nécessaires lorsque le joueur est attaqué
            attackEnemy(attackingMonster, player);
            updateAfterAttack(attackingMonster, player);

            logger.info("Joueur: " + attackingMonster.getName() + " attaque " + player.getName());

            // Mettre à jour l'affichage des points de vie du joueur
            playerHealthLabel.setText("Points de vie du joueur : " + player.getHealthPoints());

            // Afficher un message indiquant que le joueur a été attaqué
            JOptionPane.showMessageDialog(this, "Vous avez été attaqué par " + attackingMonster.getName() + " et avez subi " + attackingMonster.getAttackPoints() + " points de dégâts !");
        } else {
            System.out.println("L'ennemi n'a aucun monstre classique sur le plateau pour attaquer le joueur.");
        }
    }


    private void removeMonsterFromPanel(JPanel panel, Monster monster) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel monsterPanel = (JPanel) component;
                if (monsterPanel.getComponentCount() > 0 && monsterPanel.getComponent(0) instanceof ClickableMonster) {
                    ClickableMonster clickableMonster = (ClickableMonster) monsterPanel.getComponent(0);
                    if (clickableMonster.getMonster() == monster) {
                        panel.remove(monsterPanel);
                        logger.info("Monstre supprimé du plateau : " + monster.getName());
                        JOptionPane.showMessageDialog(this, "Le monstre a été supprimé du plateau.");
                        return; // Sortir de la boucle après avoir trouvé et supprimé le monstre
                    }
                }
            }
        }
    }


    private void useEnemySpecialAbility() {
        if (!playerTurn) {
            SpecialAbility specialAbility = enemy.getSpecialAbility();

            if (specialAbility != null && enemySpecialAbilityCount < 3) {
                Random random = new Random();
                double specialAbilityProbability = random.nextDouble();
                System.out.println("Probabilité d'utiliser une capacité spéciale : " + specialAbilityProbability);

                if (specialAbilityProbability < 0.35) {
                    // Utiliser la capacité spéciale du champion de l'ennemi
                    specialAbility.activate(enemy, player);
                    logger.info("Joueur: " + enemy.getName() + " utilise sa capacité spéciale sur " + player.getName());
                    updateHealthLabels();

                    enemySpecialAbilityCount++;

                    endGame();
                } else {
                    System.out.println("L'ennemi a décidé de ne pas utiliser sa capacité spéciale ce tour.");
                }
            } else {
                System.out.println("L'ennemi n'a pas de capacité spéciale ou a déjà utilisé sa capacité spéciale trois fois.");
            }
        } else {
            System.out.println("Ce n'est pas le tour de l'ennemi.");
        }
    }


    private void boostWithRandomMascot() {
        // Générer une probabilité de décision pour savoir si l'ennemi veut booster l'un de ses monstres ou un monstre du joueur
        Random random = new Random();
        double enemyBoostProbability = random.nextDouble();
        List<Monster> mascots = enemy.getMascots();

        // Vérifier si l'ennemi possède des mascottes
        if (mascots.isEmpty()) {
            System.out.println("L'ennemi n'a pas de mascottes pour booster.");
            return;
        }

        // Sélectionner une mascotte aléatoire
        int randomIndex = random.nextInt(mascots.size());
        Monster selectedMascot = mascots.get(randomIndex);

        // Si la probabilité est inférieure à 0.5, l'ennemi choisit de booster l'un de ses monstres
        if (enemyBoostProbability < 0.5) {
            List<Monster> enemyMonsters = enemy.getBoard().getSummonedMonsters();
            if (!enemyMonsters.isEmpty()) {
                int randomEnemyIndex = random.nextInt(enemyMonsters.size());
                Monster targetMonster = enemyMonsters.get(randomEnemyIndex);

                // Effectuez le boost en appelant la méthode boostMonster
                boostMonster(targetMonster);
                logger.info("Joueur: " + enemy.getName() + " booste " + targetMonster.getName());

                // Supprimer la mascotte utilisée
                mascots.remove(selectedMascot);
                removeMonsterFromPanel(enemyBoardPanel, selectedMascot);
            }
        } else {
            // Sinon, l'ennemi choisit de booster un monstre du joueur
            List<Monster> playerMonsters = player.getBoard().getSummonedMonsters();
            if (!playerMonsters.isEmpty()) {
                int randomPlayerIndex = random.nextInt(playerMonsters.size());
                Monster targetMonster = playerMonsters.get(randomPlayerIndex);

                // Effectuez le boost en appelant la méthode boostMonster
                boostMonster(targetMonster);
                logger.info("Joueur: " + enemy.getName() + " booste " + targetMonster.getName());

                // Supprimer la mascotte utilisée
                mascots.remove(selectedMascot);
                removeMonsterFromPanel(enemyBoardPanel, selectedMascot);
            }
        }

        // Mettez à jour l'interface utilisateur après avoir retiré la mascotte
        enemyBoardPanel.revalidate();
        enemyBoardPanel.repaint();
    }


    private void endGame() {
        if (player.getHealthPoints() <= 0 || enemy.getHealthPoints() <= 0) {
            // Afficher un message de victoire ou de défaite selon le cas
            if (enemy.getHealthPoints() <= 0) {
                logger.info("Joueur: " + player.getName() + " a gagné.");

                JOptionPane.showMessageDialog(this, "Félicitations, vous avez vaincu");
            } else {
                logger.info("Joueur: " + player.getName() + " a perdu.");
                JOptionPane.showMessageDialog(this, "Dommage, vous avez perdu.");
            }
            logger.info("Partie terminée.");
            dispose();

        }
    }


    public void updateEnemyHand() {
        enemyHandPanel.removeAll();

        List<Card> enemyHand = enemy.getHand();
        for (Card card : enemyHand) {
            ImageIcon originalImage = new ImageIcon(card.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH));
            ClickableCard cardLabel = new ClickableCard(originalImage, card);
            enemyHandPanel.add(cardLabel);
        }

        enemyHandPanel.revalidate();
        enemyHandPanel.repaint();
        logger.info("Mise à jour de la main de l'ennemi.");
    }

    private void drawCard(Deck deck) {
        if (!hasDrawnCardThisTurn) {
            Card card = deck.drawCard();
            if (card != null) {
                player.addToHand(card);
                logger.info("Joueur: " + player.getName() + " a pioché une carte.");
                updatePlayerHand();
                logger.info("Mise à jour de la main du joueur.");
                hasDrawnCardThisTurn = true;
            } else {
                logger.info("Le deck est vide.");

                deck.createDeck(); // Créer un nouveau deck
                logger.info("Nouveau deck créé.");
                player.reduceHealthPoints(5);
                logger.info("Joueur: " + player.getName() + " a perdu 5 points de vie.");

                // Si le deck est vide, créer un nouveau deck et piocher une carte
                JOptionPane.showMessageDialog(this, "Le deck est vide! Création d'un nouveau deck; Vous perdez 5 points de vie.");


                resetInfo();
                logger.info("Mise à jour des informations.");
                drawCard(deck); // Piocher une carte du nouveau deck
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vous avez déjà pioché une carte ce tour !");
        }
    }


    public void updateHandAfterPlayingCard(Card card) {
        // Retirer la carte jouée de la main du joueur
        playerHand.remove(card);

        // Mettre à jour l'affichage de la main du joueur
        playerHandPanel.removeAll();
        for (Card playerCard : playerHand) {
            ImageIcon originalImage = new ImageIcon(card.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH));

            ClickableCard cardLabel = new ClickableCard(originalImage, playerCard);
            playerHandPanel.add(cardLabel);
        }

        playerHandPanel.revalidate();
        playerHandPanel.repaint();
        logger.info("Mise à jour de la main du joueur.");
    }

    public void updateEnemyHandAfterPlayingCard(Card card) {
        // Retirer la carte jouée de la main du joueur
        enemyHand.remove(card);

        // Mettre à jour l'affichage de la main du joueur
        enemyHandPanel.removeAll();
        for (Card playerCard : enemyHand) {
            ImageIcon originalImage = new ImageIcon(card.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH));

            ClickableCard cardLabel = new ClickableCard(originalImage, playerCard);
            enemyHandPanel.add(cardLabel);
        }

        enemyHandPanel.revalidate();
        enemyHandPanel.repaint();
        logger.info("Mise à jour de la main de l'ennemi.");
    }

    private void updateHealthLabels() {
        playerHealthLabel.setText(Integer.toString(player.getHealthPoints()));
        enemyHealthLabel.setText(Integer.toString(enemy.getHealthPoints()));
    }

    public void updatePlayerHand() {
        playerHandPanel.removeAll();

        List<Card> playerHand = player.getHand();
        for (Card card : playerHand) {
            ImageIcon originalImage = new ImageIcon(card.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH));
            ClickableCard cardLabel = new ClickableCard(originalImage, card);
            playerHandPanel.add(cardLabel);
        }

        playerHandPanel.revalidate();
        playerHandPanel.repaint();

    }

    private Champion getCurrentActiveChampion() {
        return playerTurn ? player : enemy;
    }


    private void reduceProtectionTurns(List<Monster> monsters) {
        Monster championProtector = null;
        Monster nonChampionProtector = null;

        for (Monster monster : monsters) {
            if (monster.getType() == MonsterType.PROTECTOR) {
                if (monster.getTargetType() == TargetType.CHAMPION) {
                    if (championProtector == null || monster.getProtectionTurns() > championProtector.getProtectionTurns()) {
                        championProtector = monster;
                        System.out.println("Champion protector : " + championProtector.getName());
                    }
                } else {
                    if (nonChampionProtector == null || monster.getProtectionTurns() > nonChampionProtector.getProtectionTurns()) {
                        nonChampionProtector = monster;
                        System.out.println("Non champion protector : " + nonChampionProtector.getName());
                    }
                }
            }
        }

        // Réduire les tours de protection uniquement pour les monstres retenus
        if (championProtector != null) {
            championProtector.reduceProtectionTurns();
            logger.info("Le protector champion  " + championProtector.getName() + " a perdu un tour de protection.");
        }
        if (nonChampionProtector != null) {
            nonChampionProtector.reduceProtectionTurns();
            logger.info("Le protector non champion  " + nonChampionProtector.getName() + " a perdu un tour de protection.");
        }
    }


    private void chooseCardToPlay() {
        String[] cardNames = new String[playerHand.size()];
        for (int i = 0; i < playerHand.size(); i++) {
            cardNames[i] = playerHand.get(i).getName();
        }
        String selectedCardName = (String) JOptionPane.showInputDialog(GameWindow.this, "Choisissez une carte à jouer :", "Choix de carte", JOptionPane.QUESTION_MESSAGE, null, cardNames, cardNames[0]);
        Card selectedCard = null;
        for (Card card : playerHand) {
            if (card.getName().equals(selectedCardName)) {
                selectedCard = card;
                break;
            }
        }
        if (selectedCard != null) {
            playCard(selectedCard);
            logger.info("Joueur choisi  " + selectedCard.getName());
        }
    }


    public void setSelectedMonster(ClickableMonster monster) {
        if (monster.getChampion() != null && monster.getChampion().equals(player)) {
            selectedPlayerMonster = monster;
            logger.info("Monstre sélectionné");
        } else {
            selectedEnemyMonster = monster;
            logger.info("Monstre sélectionné");
        }

    }


    public void playerAttack() {
        if (playerTurn) {
            if (!hasAttackedThisTurn) {
                if (selectedPlayerMonster != null && isActive) {
                    System.out.println(selectedPlayerMonster.getMonster().getName());
                    // Récupérer le monstre attaquant
                    Monster attackingMonster = selectedPlayerMonster.getMonster();

                    // Vérifier si le monstre attaquant a le type CLASSIC
                    if (attackingMonster.getType() == MonsterType.CLASSIC) {
                        // Vérifier si un monstre protecteur est actif sur le plateau de l'ennemi
                        boolean protectorActive = isProtectionActive(enemy.getBoard().getSummonedMonsters());
                        System.out.println("Protector active? " + protectorActive);
                        if (protectorActive) {
                            for (Monster monster : enemy.getBoard().getSummonedMonsters()) {
                                if (monster.getType() == MonsterType.PROTECTOR && monster.isProtectionActive() && monster.getTargetType() == TargetType.CHAMPION) {
                                    JOptionPane.showMessageDialog(this, "Vous ne pouvez pas attaquer, un monstre " + monster.getName() + " protecteur ennemi est actif ! , Vous devez d'abord le tuer !");
                                    return;
                                }
                            }
                        }

                        // Si aucun monstre protecteur ennemi actif ne cible le champion, l'attaque peut être effectuée
                        attackEnemy(attackingMonster, enemy);
                        updateAfterAttack(attackingMonster, enemy);

                        logger.info(attackingMonster.getName() + " attaque" + enemy.getName());
                        hasAttackedThisTurn = true;

                        selectedPlayerMonster.setActive(false);
                        selectedPlayerMonster = null;
                        isActive = false;
                        enemyIconLabel.setBorder(null); // Rétablir l'apparence normale de l'icône de l'ennemi

                        endGame();

                    } else {
                        JOptionPane.showMessageDialog(this, "Vous ne pouvez pas attaquer avec ce type de monstre.");
                    }
                } else if (selectedPlayerMonster != null && selectedEnemyMonster != null) {
                    System.out.println(selectedEnemyMonster.getMonster().getName());
                    System.out.println(selectedPlayerMonster.getMonster().getName());
                    //
                    Monster attackingMonster = selectedPlayerMonster.getMonster();
                    if (attackingMonster.getType() == MonsterType.CLASSIC) {

                        // Vérifier si un monstre protecteur ennemi est actif
                        boolean protectorActive = isProtectionActive(enemy.getBoard().getSummonedMonsters());

                        if (protectorActive) {
                            for (Monster monster : enemy.getBoard().getSummonedMonsters()) {
                                if (monster.getType() == MonsterType.PROTECTOR && monster.isProtectionActive() && monster.getTargetType() == TargetType.NON_CHAMPION) {
                                    if (selectedEnemyMonster.getMonster().equals(monster)) {
                                        // Attaque du monstre protecteur ennemi actif
                                        attackEnemyMonster(attackingMonster, selectedEnemyMonster.getMonster());

                                        updateUIAfterAttack(attackingMonster, selectedEnemyMonster.getMonster());
                                        logger.info(attackingMonster.getName() + " attaque" + selectedEnemyMonster.getMonster().getName());
                                    } else {
                                        JOptionPane.showMessageDialog(this, "Vous ne pouvez pas attaquer d'autres monstres tant qu'un monstre protecteur ennemi est actif !");
                                        return; // Sortir de la méthode si un monstre protecteur est actif
                                    }
                                }
                            }
                        } else {
                            // Si aucun monstre protecteur ennemi actif, effectuer l'attaque
                            attackEnemyMonster(attackingMonster, selectedEnemyMonster.getMonster());
                            updateUIAfterAttack(attackingMonster, selectedEnemyMonster.getMonster());

                            logger.info(attackingMonster.getName() + " attaque" + selectedEnemyMonster.getMonster().getName());
                        }

                        // Vérifier si les points de vie du monstre ennemi sont <= 0
                        if (selectedEnemyMonster.getMonster().getHealthPoints() <= 0) {
                            JOptionPane.showMessageDialog(this, "Le monstre ennemi a été tué !");
                            // Supprimer le monstre ennemi du plateau de l'ennemi
                            enemy.getBoard().removeMonster(selectedEnemyMonster.getMonster());
                            logger.info(selectedEnemyMonster.getMonster().getName() + " est mort");
                            // Supprimer le JPanel contenant le monstre ennemi de l'interface
                            Container monsterPanel = selectedEnemyMonster.getParent();
                            if (monsterPanel != null && monsterPanel.getParent() == enemyBoardPanel) {
                                enemyBoardPanel.remove(monsterPanel);
                                enemyBoardPanel.revalidate();
                                enemyBoardPanel.repaint();
                                logger.info(selectedEnemyMonster.getMonster().getName() + " est supprimé du plateau de l'ennemi");
                            } else {
                                System.err.println("Le monstre ne fait pas partie du plateau de l'ennemi.");
                            }
                        }
                        hasAttackedThisTurn = true;
                        selectedPlayerMonster.setActive(false);
                        selectedEnemyMonster.setActive(false);
                        selectedPlayerMonster = null;
                        selectedEnemyMonster = null;
                    } else {
                        System.out.println(attackingMonster.getName());
                        JOptionPane.showMessageDialog(this, "Ce monstre ne peut pas attaquer.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez l'icône du monstre joueur ou un monstre ennemi pour attaquer.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vous avez déjà attaqué ce tour !");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ce n'est pas votre tour !");
        }
    }


    public void attackEnemy(Monster attacker, Champion target) {
        int damage = attacker.getAttackPoints();
        target.reduceHealthPoints(damage);
        if (target.getHealthPoints() <= 0) {
            endGame();
        }
    }

    private boolean isProtectionActive(List<Monster> monsters) {
        for (Monster monster : monsters) {
            if (monster.isProtectionActive()) {
                System.out.println("Protection active? " + monster.isProtectionActive());
                logger.info("Protection active");
                return true;
            }
        }
        logger.info("Protection inactive");
        return false;
    }


    public void attackEnemyMonster(Monster attacker, Monster target) {
        int damage = attacker.getAttackPoints();
        target.reduceHealthPoints(damage);
        if (target.getHealthPoints() <= 0) {
            target.getOwner().getBoard().removeMonster(target);
        }
    }

    public void updateUIAfterAttack(Monster attacker, Monster target) {
        System.out.println(attacker.getName() + " attaque " + target.getName() + " !");
        playerBoardPanel.repaint();
        enemyBoardPanel.repaint();
        player.setAttacking(false);
        enemy.setAttacking(false);
        logger.info("Mise à jour de l'interface");
    }


    private void updateAfterAttack(Monster attacker, Champion target) {
        System.out.println(attacker.getName() + " attaque " + target.getName() + " !");
        playerBoardPanel.repaint();
        enemyBoardPanel.repaint();
        player.setAttacking(false);
        enemy.setAttacking(false);
        logger.info("Mise à jour de l'interface");


    }

    public void updateInfo(Monster monster) {
        if (monster != null) {
            String info = "Ennemi: " + enemy.getHealthPoints() + "PV, Joueur : " + player.getHealthPoints() + "PV, Monstre: " + monster.getName() + ", Vie : " + monster.getHealthPoints() + ", Attaque: " + monster.getAttackPoints() + ", " + monster.getType();
            System.out.println(info);
            InfoLabel.setText(info);
        } else {
            resetInfo();
        }
    }

    public void resetInfo() {
        InfoLabel.setText("Ennemi: " + enemy.getHealthPoints() + "PV, Joueur : " + player.getHealthPoints() + "PV");
        logger.info("Mise à jour de l'interface");

    }


    private void useSpecialAbility() {
        if (playerTurn) {
            // Vérifie si le joueur a déjà utilisé sa capacité spéciale trois fois
            if (specialAbilityUsageCount < 3) {
                // Vérifie si le champion du joueur a une capacité spéciale et si elle n'a pas encore été utilisée dans ce tour
                if (!hasUsedSpecialAbilityThisTurn) {
                    SpecialAbility specialAbility = player.getSpecialAbility();
                    if (specialAbility != null) {
                        // Utiliser la capacité spéciale du champion du joueur
                        specialAbility.activate(player, enemy);
                        logger.info("Joueur utilise sa capacité spéciale");

                        // Mettre à jour les informations d'affichage après l'utilisation de la capacité spéciale
                        updateHealthLabels();
                        logger.info("Mise à jour des points de vie");

                        // Incrémenter le compteur d'utilisation de la capacité spéciale
                        specialAbilityUsageCount++;

                        // Vérifie si le joueur a atteint la limite d'utilisation de la capacité spéciale
                        if (specialAbilityUsageCount == 3) {
                            JOptionPane.showMessageDialog(this, "Vous avez atteint la limite d'utilisation de votre capacité spéciale !");
                        }

                        // Marquer que le joueur a utilisé sa capacité spéciale dans ce tour
                        hasUsedSpecialAbilityThisTurn = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Ce champion n'a pas de capacité spéciale !");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vous avez déjà utilisé votre capacité spéciale ce tour !");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vous avez atteint la limite d'utilisation de votre capacité spéciale !");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ce n'est pas votre tour !");
        }
    }

    private void useHeal() {
        // Récupérer les monstres soigneurs du champion actif
        List<HealerMonster> healers = player.getHealers();

        // Si aucun soigneur n'est disponible, afficher un message et retourner
        if (healers.isEmpty()) {
            logger.info("Aucun monstre soigneur disponible");
            JOptionPane.showMessageDialog(this, "Aucun monstre soigneur disponible.");
            return;
        }

        // Convertir la liste de monstres soigneurs en une liste de noms de monstres
        List<String> healerNames = new ArrayList<>();
        for (Monster healer : healers) {
            healerNames.add(healer.getName());
        }

        // Afficher une boîte de dialogue pour demander à l'utilisateur de sélectionner le monstre soigneur
        String selectedHealerName = (String) JOptionPane.showInputDialog(
                this,
                "Sélectionnez le monstre soigneur à utiliser :",
                "Sélectionner le monstre soigneur",
                JOptionPane.QUESTION_MESSAGE,
                null,
                healerNames.toArray(),
                healerNames.get(0));

        // Si l'utilisateur a sélectionné un soigneur, continuer
        if (selectedHealerName != null) {
            // Trouver le monstre soigneur correspondant au nom sélectionné
            HealerMonster selectedHealer = null;
            for (HealerMonster healer : healers) {
                if (healer.getName().equals(selectedHealerName)) {
                    logger.info("Sélectionné le monstre soigneur " + selectedHealerName);
                    selectedHealer = healer;
                    break;
                }
            }

            if (selectedHealer != null) {
                // Afficher une liste de choix pour la cible du soin
                String[] options = {"Joueur", "Ennemi", "Monstre"};
                int choice = JOptionPane.showOptionDialog(this,
                        "Qui voulez-vous soigner ?\n Le monstre soigneur a " + selectedHealer.getHealthPoints() + " PV.\n Il ajoutera " + selectedHealer.getHealAmount() + " PV à votre cible mais perdra le même nombre de PV.",
                        "Choix de la cible du soin",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                // Traiter la sélection de la cible du soin
                switch (choice) {
                    case 0: // Soigner le joueur
                        player.heal(selectedHealer.getHealAmount());
                        logger.info("Joueur soigne le joueur");
                        selectedHealer.setHealthPoints(selectedHealer.getHealthPoints() - selectedHealer.getHealAmount());
                        JOptionPane.showMessageDialog(this, "Le joueur a été soigné de " + selectedHealer.getHealAmount() + " PV. Ses PV sont à présent à " + player.getHealthPoints() + " PV.");
                        break;
                    case 1: // Soigner l'ennemi
                        enemy.heal(selectedHealer.getHealAmount());
                        logger.info("Joueur soigne l'ennemi");
                        selectedHealer.setHealthPoints(selectedHealer.getHealthPoints() - selectedHealer.getHealAmount());

                        JOptionPane.showMessageDialog(this, "L'ennemi a été soigné. de " + selectedHealer.getHealAmount() + " PV. Ses PV sont à présent à " + enemy.getHealthPoints() + " PV.");
                        break;
                    case 2: // Soigner un monstre
                        // Récupérer les monstres restants
                        List<Monster> remainingMonsters = new ArrayList<>(player.getBoard().getSummonedMonsters());

                        // Retirer le soigneur de la liste des monstres restants
                        remainingMonsters.remove(selectedHealer);

                        // Si aucun monstre restant, afficher un message et retourner
                        if (remainingMonsters.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Aucun monstre à soigner disponible.");
                            return;
                        }

                        // Convertir la liste de monstres restants en une liste de noms de monstres
                        List<String> remainingMonsterNames = new ArrayList<>();
                        for (Monster monster : remainingMonsters) {
                            remainingMonsterNames.add(monster.getName());
                        }

                        // Afficher une boîte de dialogue pour demander à l'utilisateur de sélectionner la cible du soin
                        String selectedTargetMonsterName = (String) JOptionPane.showInputDialog(
                                this,
                                "Sélectionnez le monstre à soigner :",
                                "Sélectionner la cible du soin",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                remainingMonsterNames.toArray(),
                                remainingMonsterNames.get(0));

                        // Trouver le monstre cible correspondant au nom sélectionné
                        Monster selectedTargetMonster = null;
                        for (Monster monster : remainingMonsters) {
                            if (monster.getName().equals(selectedTargetMonsterName)) {
                                selectedTargetMonster = monster;
                                logger.info("Sélectionné le monstre " + selectedTargetMonsterName);
                                break;
                            }
                        }

                        if (selectedTargetMonster != null) {
                            selectedTargetMonster.heal(selectedHealer.getHealAmount());
                            logger.info("Joueur soigne le monstre");
                            selectedHealer.setHealthPoints(selectedHealer.getHealthPoints() - selectedHealer.getHealAmount());

                            JOptionPane.showMessageDialog(this, "Le monstre " + selectedTargetMonster.getName() + " a été soigné de " + selectedHealer.getHealAmount() + " PV. Ses PV sont à présent à " + selectedTargetMonster.getHealthPoints() + " PV.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Opération annulée.");
                        }
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Opération annulée.");
                        break;
                }
            }
        }
    }


    private void useMascot() {
        Champion activeChampion = getCurrentActiveChampion();
        // Récupérer les mascottes du champion actif
        List<Monster> mascots = player.getMascots();

        // Si aucune mascotte n'est disponible, afficher un message et retourner
        if (mascots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune mascotte disponible.");
            return;
        }

        // Convertir la liste de mascotte en une liste de noms de monstres
        List<String> mascotNames = new ArrayList<>();
        for (Monster mascot : mascots) {
            mascotNames.add(mascot.getName());
        }

        // Afficher une boîte de dialogue pour demander à l'utilisateur de sélectionner la mascotte à utiliser
        String selectedMascotName = (String) JOptionPane.showInputDialog(
                this,
                "Sélectionnez la mascotte à utiliser \n La mascotte disparaitra après l'utilisation.",
                "Sélectionner la mascotte",
                JOptionPane.QUESTION_MESSAGE,
                null,
                mascotNames.toArray(),
                mascotNames.get(0));

        // Si l'utilisateur a sélectionné une mascotte, continuer
        if (selectedMascotName != null) {
            // Trouver le monstre mascot correspondant au nom sélectionné
            Monster selectedMascot = null;
            for (Monster mascot : mascots) {
                if (mascot.getName().equals(selectedMascotName)) {
                    selectedMascot = mascot;
                    logger.info("Sélectionné la mascotte " + selectedMascotName);
                    break;
                }
            }

            if (selectedMascot != null) {
                // Afficher une liste de choix pour la cible de la mascotte
                String[] options = {"Mes Monstres", "Les Monstres Ennemis"};
                int choice = JOptionPane.showOptionDialog(this,
                        "Qui voulez-vous booster ?",
                        "Choix de la cible de la mascotte",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                // Traiter le choix de la cible
                switch (choice) {
                    case 0:

                        // Afficher la liste des monstres du joueur
                        showMyMonsters(selectedMascot);
                        logger.info("Affichage des monstres du joueur");

                        // Traiter les monstres du joueur
                        break;
                    case 1:
                        // Afficher la liste des monstres ennemis
                        showEnemyMonsters(selectedMascot);
                        logger.info("Affichage des monstres ennemis");

                        // Traiter les monstres ennemis
                        break;
                    default:
                        // Gérer les autres cas
                        break;
                }
// Supprimer la mascotte du joueur
                activeChampion.getBoard().getSummonedMonsters().remove(selectedMascot);
                logger.info("Mascotte supprimée du plateau");

// Supprimer le JPanel contenant la mascotte du panneau du plateau du joueur
                for (Component component : playerBoardPanel.getComponents()) {
                    if (component instanceof JPanel) {
                        JPanel monsterPanel = (JPanel) component;
                        if (monsterPanel.getComponentCount() > 0 && monsterPanel.getComponent(0) instanceof ClickableMonster) {
                            ClickableMonster monster = (ClickableMonster) monsterPanel.getComponent(0);
                            if (monster.getMonster() == selectedMascot) {
                                playerBoardPanel.remove(monsterPanel);
                                enemyBoardPanel.remove(monsterPanel);
                                JOptionPane.showMessageDialog(this, "La mascotte a été supprimée.");
                                break; // Nous avons trouvé et supprimé le JPanel contenant le monstre, donc sortir de la boucle
                            }
                        }
                    }
                }

                // Rafraîchir le panneau du plateau du joueur pour refléter les modifications
                playerBoardPanel.revalidate();
                playerBoardPanel.repaint();
                enemyBoardPanel.revalidate();
                enemyBoardPanel.repaint();
                logger.info("Rafraîchissement du panneau du plateau");

            }
        }
    }

    private void showMyMonsters(Monster mascot) {
        List<Monster> playerMonsters = player.getBoard().getSummonedMonsters();

        // Filtrer les monstres de type "mascotte"
        List<Monster> filteredPlayerMonsters = playerMonsters.stream()
                .filter(monster -> !monster.getType().equals(MonsterType.MASCOT))
                .collect(Collectors.toList());

        showMonsterListDialog("Mes Monstres", filteredPlayerMonsters, new ArrayList<>(), mascot);
    }

    private void showEnemyMonsters(Monster mascot) {
        List<Monster> enemyMonsters = enemy.getBoard().getSummonedMonsters();

        // Filtrer les monstres de type "mascotte"
        List<Monster> filteredEnemyMonsters = enemyMonsters.stream()
                .filter(monster -> !monster.getType().equals(MonsterType.MASCOT))
                .collect(Collectors.toList());

        showMonsterListDialog("Les Monstres Ennemis", new ArrayList<>(), filteredEnemyMonsters, mascot);
    }


    private void showMonsterListDialog(String title, List<Monster> playerMonsters, List<Monster> enemyMonsters, Monster selectedMascot) {
        List<Monster> monstersToShow;

        // Déterminez quels monstres afficher en fonction du titre et du champion actif
        if (title.equals("Mes Monstres") && playerMonsters != null) {
            monstersToShow = playerMonsters;
        } else if (title.equals("Les Monstres Ennemis") && enemyMonsters != null) {
            monstersToShow = enemyMonsters;
        } else {
            // Si aucune liste de monstres n'est disponible, affichez un message d'erreur et retournez
            JOptionPane.showMessageDialog(this, "Aucun monstre disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construisez le tableau de noms de monstres pour la liste déroulante
        String[] monsterNames = new String[monstersToShow.size()];
        for (int i = 0; i < monstersToShow.size(); i++) {
            monsterNames[i] = monstersToShow.get(i).getName();
        }

        // Créez une liste déroulante avec les noms de monstres
        JComboBox<String> monsterComboBox = new JComboBox<>(monsterNames);

        // Affichez une boîte de dialogue pour permettre à l'utilisateur de choisir le monstre à booster
        int choice = JOptionPane.showOptionDialog(this,
                monsterComboBox,
                "Choix de la cible du boost",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null);

        // Traitez le choix de l'utilisateur
        if (choice == JOptionPane.OK_OPTION) {
            int selectedIndex = monsterComboBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < monstersToShow.size()) {
                Monster selectedMonster = monstersToShow.get(selectedIndex);
                boostMonster(selectedMonster);
                logger.info("Boost du monstre " + selectedMonster.getName());
            }
        }
    }


    public void boostMonster(Monster targetMonster) {
        Champion activeChampion = getCurrentActiveChampion();
        if (targetMonster.getOwner().equals(activeChampion)) {
            if (targetMonster.getType() == MonsterType.CLASSIC) {
                int newAttackPoints = targetMonster.getAttackPoints() + 10; // Par exemple, augmentez les points d'attaque de 10
                targetMonster.setAttackPoints(newAttackPoints);

                JOptionPane.showMessageDialog(this, "Le monstre " + targetMonster.getName() + " a été boosté.\n Son pouvoir d'attaque est maintenant à " + newAttackPoints + " points.");

            } else if (targetMonster.getType() == MonsterType.PROTECTOR) {
                int protectionTurns = targetMonster.getProtectionTurns() + 2; // Par exemple, augmentez les tours de protection de 2
                targetMonster.setProtectionTurns(protectionTurns);

                JOptionPane.showMessageDialog(this, "Le monstre " + targetMonster.getName() + " a été boosté.\n Son nombre de tours de protection est maintenant à " + protectionTurns + " tours.");

            } else if (targetMonster.getType() == MonsterType.HEALER) {
                // Si le monstre est de type HEALER, augmentez le healAmount
                int newHealAmount = ((HealerMonster) targetMonster).getHealAmount() + 5; // Par exemple, augmentez le healAmount de 5
                ((HealerMonster) targetMonster).setHealAmount(newHealAmount);

                JOptionPane.showMessageDialog(this, "Le monstre " + targetMonster.getName() + " a été boosté.\n Son nombre de points de soin est maintenant à " + newHealAmount + " points.");


            }
        } else {
            if (targetMonster.getType() == MonsterType.CLASSIC) {
                int newAttackPoints = targetMonster.getAttackPoints() - 10;
                if (newAttackPoints < 0) {
                    newAttackPoints = 0;
                }
                targetMonster.setAttackPoints(newAttackPoints);

                JOptionPane.showMessageDialog(this, "Le monstre " + targetMonster.getName() + " a été réduit.\n Son pouvoir d'attaque est maintenant à " + newAttackPoints + " points.");

            } else if (targetMonster.getType() == MonsterType.PROTECTOR) {
                int protectionTurns = targetMonster.getProtectionTurns() - 2;
                if (protectionTurns < 0) {
                    protectionTurns = 0;
                }
                targetMonster.setProtectionTurns(protectionTurns);
                activeChampion.getBoard().getSummonedMonsters().remove(targetMonster);
                JOptionPane.showMessageDialog(this, "Le monstre " + targetMonster.getName() + " a été réduit.\n Son nombre de tours de protection est maintenant à " + protectionTurns + " tours.");

            } else if (targetMonster.getType() == MonsterType.HEALER) {
                // Si le monstre est de type HEALER, augmentez le healAmount
                int newHealAmount = ((HealerMonster) targetMonster).getHealAmount() - 5;
                if (newHealAmount < 0) {
                    newHealAmount = 0;
                }
                ((HealerMonster) targetMonster).setHealAmount(newHealAmount);
                activeChampion.getBoard().getSummonedMonsters().remove(targetMonster);
                JOptionPane.showMessageDialog(this, "Le monstre " + targetMonster.getName() + " a été réduit.\n Son nombre de points de soin est maintenant à " + newHealAmount + " points.");

            }
        }


    }


    // Classe BackgroundPanel pour afficher une image de fond
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Dessiner l'image de fond
            ImageIcon backgroundImage = new ImageIcon("src/Icons/bg.png");
            Image img = backgroundImage.getImage();
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

