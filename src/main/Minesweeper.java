package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends JPanel {
    private final int ROWS;
    private final int COLS;
    private final int MINES;

    private Cell[][] board;
    private JButton[][] buttons;
    private boolean gameOver = false;
    private boolean minesGenerated = false;

    private final Map<String, ImageIcon> icons = new HashMap<>();

    private int flagsLeft;
    private JLabel bombCounter;
    private JLabel timerLabel;
    private int secondsPassed = 0;
    private Timer timer;

    class Cell {
        boolean mine = false;
        boolean revealed = false;
        boolean flagged = false;
        boolean questioned = false;
        boolean correctlyFlagged = false;
        int adjacentMines = 0;
    }

    public Minesweeper(int rows, int cols, int mines) {
        this.ROWS = rows;
        this.COLS = cols;
        this.MINES = mines;

        this.flagsLeft = MINES;

        setLayout(new BorderLayout());
        loadIcons();
        initBoard();
        createUI();
        setVisible(true);
    }

    public Minesweeper() {
        this(9, 9, 10);
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
        JPanel topBar = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = icons.get("ms_blank").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        topBar.setPreferredSize(new Dimension(800, 40));

        bombCounter = new JLabel("Bombs: " + flagsLeft);
        bombCounter.setFont(new Font("Arial", Font.BOLD, 16));
        bombCounter.setForeground(Color.RED);
        bombCounter.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.BLUE);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        timer = new Timer();

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

    private void handleClick(int r, int c) {
        if (!minesGenerated) {
            generateMines(r, c);
            startTimer();
            minesGenerated = true;
        }
        revealCell(r, c);
        checkWin();
    }

    private void generateMines(int avoidR, int avoidC) {
        Random rand = new Random();
        int placed = 0;
        while (placed < MINES) {
            int r = rand.nextInt(ROWS);
            int c = rand.nextInt(COLS);
            if ((r == avoidR && c == avoidC) || board[r][c].mine) continue;
            board[r][c].mine = true;
            placed++;
        }
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c].adjacentMines = countAdjacentMines(r, c);
            }
        }
    }

    private int countAdjacentMines(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nc < 0 || nr >= ROWS || nc >= COLS) continue;
                if (board[nr][nc].mine) count++;
            }
        return count;
    }

    private void revealCell(int r, int c) {
        Cell cell = board[r][c];
        if (cell.revealed || cell.flagged) return;

        cell.revealed = true;
        buttons[r][c].repaint();

        if (cell.mine) {
            gameOver = true;
            revealAllMines();
            timer.cancel();
            JOptionPane.showMessageDialog(this, "Game Over! You hit a mine!");
            return;
        }

        if (cell.adjacentMines == 0) {
            for (int dr = -1; dr <= 1; dr++)
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr, nc = c + dc;
                    if (nr < 0 || nc < 0 || nr >= ROWS || nc >= COLS) continue;
                    if (!board[nr][nc].revealed)
                        revealCell(nr, nc);
                }
        }
    }

    private void revealAllMines() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = board[r][c];
                if (cell.mine) {
                    cell.revealed = true;
                    buttons[r][c].repaint();
                }
            }
        }
    }

    private void checkWin() {
        int revealedCount = 0;
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                Cell cell = board[r][c];
                if (cell.revealed) revealedCount++;
            }
        if (revealedCount == ROWS * COLS - MINES) {
            gameOver = true;
            timer.cancel();
            JOptionPane.showMessageDialog(this, "Congratulations! You cleared the minefield!");
        }
    }

    private void startTimer() {
        timer = new Timer();
        secondsPassed = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    secondsPassed++;
                    timerLabel.setText("Time: " + secondsPassed + "s");
                });
            }
        }, 1000, 1000);
    }

    private void updateBombCounter() {
        bombCounter.setText("Bombs: " + flagsLeft);
    }
}
