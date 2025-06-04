package main;

import javax.swing.*;
import java.awt.*;
import ui.Button;

public class Menu extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Minesweeper gamePanel;
    private int width = 800, height = 600;
    private JPanel menuPanel = new JPanel(new BorderLayout());

    public Menu() {
        setupFrame();
        buidingLayout();
        createPanels();
        createButtons();
        setVisible(true);
    }

    private void startGame() {
        cardLayout.show(mainPanel, "Game");
    }

    private void setupFrame() {
        setTitle("Minesweeper");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void buidingLayout() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }

    private void createPanels() {

        gamePanel = new Minesweeper();
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(gamePanel, "Game");
    }

    private void createButtons() {
        JPanel buttonPanel = new JPanel(null);

        JLabel titleLabel = new JLabel("MINESWEEPER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        menuPanel.add(titleLabel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);

        buttonPanel.setPreferredSize(new Dimension(width, height));

        int x = width / 4;
        int y = height / 2 - 60;
        int w = width / 2;
        int h = 50;
        Button startButton = new Button("Start", x, y, w, h);
        Button exitButton = new Button("Exit", x, y + 70, w, h);

        startButton.getButton().addActionListener(e -> startGame());
        exitButton.getButton().addActionListener(e -> System.exit(0));

        buttonPanel.add(startButton.getButton());
        buttonPanel.add(exitButton.getButton());
    }

}
