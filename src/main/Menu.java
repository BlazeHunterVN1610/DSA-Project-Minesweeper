package main;

import javax.swing.*;
import java.awt.*;
import ui.Button;

public class Menu extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Minesweeper gamePanel;
    private int width = 800, height = 800;
    private JPanel menuPanel = new JPanel(new BorderLayout());

    public Menu() {
        setupFrame();
        buildLayout();
        createPanels();
        createButtons();
        setVisible(true);
    }

    private void startGame(int rows, int cols, int mines) {
        if (gamePanel != null) {
            mainPanel.remove(gamePanel);
        }
        gamePanel = new Minesweeper(rows, cols, mines);
        mainPanel.add(gamePanel, "Game");
        cardLayout.show(mainPanel, "Game");
        revalidate();
        repaint();
    }

    private void setupFrame() {
        setTitle("Minesweeper");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void buildLayout() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }

    private void createPanels() {
        mainPanel.add(menuPanel, "Menu");
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
        int y = height / 2 - 110;
        int w = width / 2;
        int h = 50;
        int spacing = 60;

        // Difficulty buttons
        Button easyBtn = new Button("Easy (13x13, 20 mines)", x, y, w, h);
        Button normalBtn = new Button("Normal (15x15, 30 mines)", x, y + spacing, w, h);
        Button hardBtn = new Button("Hard (17x17, 42 mines)", x, y + spacing * 2, w, h);
        Button expertBtn = new Button("Expert (19x19, 68 mines)", x, y + spacing * 3, w, h);

        Button exitBtn = new Button("Exit", x, y + spacing * 4 + 20, w, h);

        easyBtn.getButton().addActionListener(e -> startGame(13, 13, 20));
        normalBtn.getButton().addActionListener(e -> startGame(15, 15, 30));
        hardBtn.getButton().addActionListener(e -> startGame(17, 17, 42));
        expertBtn.getButton().addActionListener(e -> startGame(19, 19, 68));

        exitBtn.getButton().addActionListener(e -> System.exit(0));

        buttonPanel.add(easyBtn.getButton());
        buttonPanel.add(normalBtn.getButton());
        buttonPanel.add(hardBtn.getButton());
        buttonPanel.add(expertBtn.getButton());
        buttonPanel.add(exitBtn.getButton());
    }
}
