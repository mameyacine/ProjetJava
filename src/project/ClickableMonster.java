package project;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.JLabel;

import java.awt.*;
import java.awt.event.*;

public class ClickableMonster extends JPanel {
    private Monster monster;
    private Champion player;
    private Champion enemy;
    private boolean isActive;
    private GameWindow gameWindow;



    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.YELLOW, 2);

    public ClickableMonster(ImageIcon originalImage, Monster monster, Champion player, Champion enemy) {
        this.monster = monster;
        this.player = player;
        this.enemy = enemy;
        this.isActive = false;

        setLayout(new BorderLayout());

        JLabel imageLabel = new JLabel(originalImage);
        add(imageLabel, BorderLayout.CENTER);


        // Add mouse listener to handle mouse enter event
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                showMonsterInfo();
                handleEnter() ;
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (gameWindow != null) {
                    gameWindow.resetInfo(); // Appeler resetMonsterInfo() de GameWindow.java
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(); // Handle click on the monster
            }
        });

        addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                // Recherche de la fenêtre parente de ClickableMonster
                Container parent = getParent();
                while (parent != null && !(parent instanceof GameWindow)) {
                    parent = parent.getParent();
                }
                if (parent instanceof GameWindow) {
                    gameWindow = (GameWindow) parent; // Stocker la référence à GameWindow.java
                }
            }
        });
    }

    public Monster getMonster() {
        return monster;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        if (active) {
            setBorder(SELECTED_BORDER);
        } else {
            setBorder(null);
        }
    }

    private void handleClick() {
        if (!isActive) {
            isActive = true;
            setActive(true);
            ((GameWindow) SwingUtilities.getWindowAncestor(this)).setSelectedMonster(this);
        } else {
            isActive = false;
            setActive(false);
            ((GameWindow) SwingUtilities.getWindowAncestor(this)).setSelectedMonster(null);
        }
    }

    private void handleEnter() {

        SwingUtilities.invokeLater(() -> {
            ((GameWindow) SwingUtilities.getWindowAncestor(this)).updateInfo(monster);
        });
    }



    public Champion getChampion() {
        if (isActive && monster != null) {
            return monster.getOwner(); // Suppose que le monstre a une méthode getOwner() pour obtenir son propriétaire
        } else {
            return null;
        }
    }

    private void showMonsterInfo() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof GameWindow)) {
            parent = parent.getParent();
        }
        if (parent instanceof GameWindow) {
            ((GameWindow) parent).updateInfo(monster);
        }
    }









}
