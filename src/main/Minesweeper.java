package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

import ui.Button;

public class Minesweeper extends JPanel {
    private int ROWS, COLS, MINES;
    private Cell[][] board;
    private JButton[][] buttons;
    private boolean gameOver = false;
    private boolean minesGenerated = false;

    private final Map<String, ImageIcon> icons = new HashMap<>();
    private int flagsLeft;
    private JLabel bombCounter;
    private JLabel timerLabel;
    private int secondsPassed = 0;
    private javax.swing.Timer swingTimer;

    private Menu menu;
    private Runnable returnToMenuCallback;

    class Cell {
        boolean mine = false;
        boolean revealed = false;
        boolean flagged = false;
        boolean questioned = false;
        boolean correctlyFlagged = false;
        int adjacentMines = 0;
    }

    public Minesweeper(int rows, int cols, int mines, Menu menu, Runnable returnToMenuCallback) {
        this.ROWS = rows;
        this.COLS = cols;
        this.MINES = mines;
        this.menu = menu;
        this.returnToMenuCallback = returnToMenuCallback;
        this.flagsLeft = MINES;

        setLayout(new BorderLayout());
        loadIcons();
        initBoard();
        createUI();
        setVisible(true);
    }

    private void loadIcons() {
        String[] files = {
                "ms_blank", "ms_flag", "ms_bomb", "ms_unknown", "ms_correct_bomb",
                "ms_0", "ms_1", "ms_2", "ms_3", "ms_4", "ms_5", "ms_6", "ms_7", "ms_8"
        };
        for (String name : files) {
            try {
                File f = new File("src/assets/textures/" + name + ".png");
                if (!f.exists()) throw new Exception("Missing: " + name + ".png");
                icons.put(name, new ImageIcon(f.getAbsolutePath()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load image: " + name + "\n" + e.getMessage());
            }
        }
    }

    private void initBoard() {
        board = new Cell[ROWS][COLS];
        buttons = new JButton[ROWS][COLS];
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                board[r][c] = new Cell();
    }

    private void createUI() {
        ImageIcon blankIcon = icons.get("ms_blank");

        bombCounter = new JLabel("Bombs: " + flagsLeft);
        bombCounter.setFont(new Font("Arial", Font.BOLD, 16));
        bombCounter.setForeground(Color.RED);
        bombCounter.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.BLUE);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel topBar = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (blankIcon != null) {
                    g.drawImage(blankIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(800, 40));
        topBar.add(bombCounter, BorderLayout.WEST);
        topBar.add(timerLabel, BorderLayout.EAST);

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS));

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                final int row = r, col = c;

                JButton btn = new JButton() {
                    public Dimension getPreferredSize() {
                        return new Dimension(40, 40);
                    }

                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        ImageIcon icon = getCellIcon(row, col);
                        if (icon != null) {
                            g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };

                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setOpaque(false);

                btn.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (gameOver || board[row][col].revealed) return;
                        if (SwingUtilities.isRightMouseButton(e)) {
                            cycleFlag(row, col);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            handleClick(row, col);
                        }
                    }
                });

                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        add(topBar, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
    }

    private ImageIcon getCellIcon(int r, int c) {
        Cell cell = board[r][c];
        if (!cell.revealed) {
            if (cell.flagged) return icons.get("ms_flag");
            if (cell.questioned) return icons.get("ms_unknown");
            return icons.get("ms_blank");
        } else {
            if (cell.correctlyFlagged) return icons.get("ms_correct_bomb");
            if (cell.mine) return icons.get("ms_bomb");
            return icons.get("ms_" + cell.adjacentMines);
        }
    }

    private void cycleFlag(int r, int c) {
        Cell cell = board[r][c];
        if (cell.revealed) return;

        if (!cell.flagged && !cell.questioned) {
            cell.flagged = true;
            flagsLeft--;
        } else if (cell.flagged) {
            cell.flagged = false;
            cell.questioned = true;
            flagsLeft++;
        } else {
            cell.questioned = false;
        }
        updateBombCounter();
        buttons[r][c].repaint();
    }

    private void updateBombCounter() {
        bombCounter.setText("Bombs: " + flagsLeft);
    }

    private void updateTimer() {
        secondsPassed++;
        timerLabel.setText("Time: " + secondsPassed + "s");
    }

    private void startTimer() {
        swingTimer = new javax.swing.Timer(1000, e -> updateTimer());
        swingTimer.start();
    }

    private void handleClick(int r, int c) {
        Cell cell = board[r][c];
        if (gameOver || cell.revealed || cell.flagged) return;

        if (!minesGenerated) {
            generateMines(r, c);
            startTimer();
        }

        reveal(r, c);

        if (cell.mine) {
            gameOver = true;
            swingTimer.stop(); // ⛔️ Stop the timer immediately
            revealAllMines();
            showGameOverDialog("Game Over! Play again?");
        } else if (checkWin()) {
            gameOver = true;
            swingTimer.stop(); // ⛔️ Stop the timer immediately
            revealAllMines();
            showGameOverDialog("You Win! Play again?");
        }
    }

    private void showGameOverDialog(String message) {
        String[] options = {"Restart", "Return to Menu", "Exit"};
        int choice = JOptionPane.showOptionDialog(
                this, message, "Game Ended",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            swingTimer.stop();
            resetGame();
        } else if (choice == JOptionPane.NO_OPTION) {
            swingTimer.stop();
            returnToMenuCallback.run();
        } else {
            System.exit(0);
        }
    }

    private void reveal(int r, int c) {
        if (!inBounds(r, c) || board[r][c].revealed || board[r][c].flagged) return;

        board[r][c].revealed = true;
        buttons[r][c].repaint();

        if (board[r][c].adjacentMines == 0 && !board[r][c].mine) {
            for (int dr = -1; dr <= 1; dr++)
                for (int dc = -1; dc <= 1; dc++)
                    if (dr != 0 || dc != 0)
                        reveal(r + dr, c + dc);
        }
    }

    private void generateMines(int safeRow, int safeCol) {
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (!(r == safeRow && c == safeCol))
                    positions.add(new int[]{r, c});
        Collections.shuffle(positions, new Random());
        for (int i = 0; i < MINES; i++) {
            int[] pos = positions.get(i);
            board[pos[0]][pos[1]].mine = true;
        }
        calculateAdjacency();
        minesGenerated = true;
    }

    private void calculateAdjacency() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c].mine) continue;
                int count = 0;
                for (int dr = -1; dr <= 1; dr++)
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr, nc = c + dc;
                        if (inBounds(nr, nc) && board[nr][nc].mine)
                            count++;
                    }
                board[r][c].adjacentMines = count;
            }
        }
    }

    private void revealAllMines() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = board[r][c];
                if (cell.mine && cell.flagged) {
                    cell.revealed = true;
                    cell.correctlyFlagged = true;
                } else if (cell.mine) {
                    cell.revealed = true;
                }
                buttons[r][c].repaint();
            }
        }
    }

    private boolean checkWin() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (!board[r][c].mine && !board[r][c].revealed)
                    return false;
        return true;
    }

    private void resetGame() {
        removeAll();
        secondsPassed = 0;
        minesGenerated = false;
        gameOver = false;
        flagsLeft = MINES;
        swingTimer = new javax.swing.Timer(1000, e -> updateTimer());

        initBoard();
        createUI();

        revalidate();
        repaint();
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }
}
