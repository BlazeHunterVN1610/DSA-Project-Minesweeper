package main;

import javax.swing.*;
import java.awt.*;
import ui.Button;

public class Menu extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel buttonPanel;
    private Minesweeper gamePanel;

    private int width = 800, height = 800;
    private ImageIcon backgroundImage = new ImageIcon("src/assets/textures/ms_menu.png");

    public Menu() {
        setupFrame();
        buildingLayout();
        createPanels();
        createButtons();
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Minesweeper");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void buildingLayout() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }

    private void createPanels() {
        menuPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        buttonPanel = new JPanel(null);
        buttonPanel.setOpaque(false);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(menuPanel, "Menu");
    }

    private void createButtons() {
        buttonPanel.removeAll();

        int w = 200;
        int h = 50;
        int x = (width - w) / 2;
        int startY = height - 180;

        Button startButton = new Button("Start", x, startY, w, h);
        Button exitButton = new Button("Exit", x, startY + 70, w, h);

        startButton.getButton().addActionListener(e -> createDifficultyButtons());
        exitButton.getButton().addActionListener(e -> System.exit(0));

        buttonPanel.add(startButton.getButton());
        buttonPanel.add(exitButton.getButton());

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void createDifficultyButtons() {
        buttonPanel.removeAll();

        int w = 200;
        int h = 50;
        int x = (width - w) / 2;
        int startY = height - 280;
        int spacing = 60;

        String[] levels = {"Easy", "Normal", "Hard", "Expert"};
        int[][] settings = {
                {13, 13, 20},
                {15, 15, 30},
                {17, 17, 42},
                {19, 19, 68}
        };

        for (int i = 0; i < levels.length; i++) {
            Button levelBtn = new Button(levels[i], x, startY + (i * spacing), w, h);
            int rows = settings[i][0];
            int cols = settings[i][1];
            int mines = settings[i][2];

            levelBtn.getButton().addActionListener(e -> {
                gamePanel = new Minesweeper(rows, cols, mines, this, this::returnToMenu);
                mainPanel.add(gamePanel, "Game");
                cardLayout.show(mainPanel, "Game");
            });

            buttonPanel.add(levelBtn.getButton());
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void returnToMenu() {
        mainPanel.remove(gamePanel);
        gamePanel = null;
        cardLayout.show(mainPanel, "Menu");
        createButtons();
    }
}
