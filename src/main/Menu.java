package main;

import javax.swing.*;
import java.awt.*;
import ui.Button;

public class Menu extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Minesweeper gamePanel;
    private int width = 800, height = 600;

    public Menu() {
        setTitle("Minesweeper");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // ==== MENU PANEL ====
        JPanel menuPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("MINESWEEPER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        menuPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(null);
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

        menuPanel.add(buttonPanel, BorderLayout.CENTER);

        // ==== GAME PANEL ====
        gamePanel = new Minesweeper();

        // ==== ADD TO MAIN PANEL ====
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);
        setVisible(true);
    }

    protected void startGame() {
        cardLayout.show(mainPanel, "Game");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::new);
    }
}
