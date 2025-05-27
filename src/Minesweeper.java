import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.File;

public class Minesweeper extends JFrame {
    private final int ROWS = 9;
    private final int COLS = 9;
    private final int MINES = 10;
    private Cell[][] board = new Cell[ROWS][COLS];
    private JButton[][] buttons = new JButton[ROWS][COLS];
    private boolean gameOver = false;
    private boolean minesGenerated = false;

    private ImageIcon bombIcon;

    class Cell {
        boolean mine = false;
        boolean revealed = false;
        int adjacentMines = 0;
    }

    public Minesweeper() {
        setTitle("Minesweeper");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS, COLS));

        loadIcons();
        initBoard();
        createButtons();

        setVisible(true);
    }

    private void loadIcons() {
        try {
            File imageFile = new File("src/assets/bomb.png");
            if (!imageFile.exists()) {
                throw new Exception("Image not found: " + imageFile.getAbsolutePath());
            }

            ImageIcon rawIcon = new ImageIcon(imageFile.getAbsolutePath());
            Image scaledImage = rawIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            bombIcon = new ImageIcon(scaledImage);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load bomb icon from 'assets/bomb.png'\n" + e.getMessage());
            bombIcon = null;
        }
    }

    private void initBoard() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                board[r][c] = new Cell();
    }

    private void generateMines(int safeRow, int safeCol) {
        java.util.List<int[]> positions = new java.util.ArrayList<>();

        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (!(r == safeRow && c == safeCol))
                    positions.add(new int[]{r, c});

        Random rand = new Random();
        for (int i = positions.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = positions.get(i);
            positions.set(i, positions.get(j));
            positions.set(j, temp);
        }

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

    private void createButtons() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                JButton btn = new JButton();
                buttons[r][c] = btn;
                int row = r, col = c;
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                btn.addActionListener(e -> handleClick(row, col));
                add(btn);
            }
        }
    }

    private void handleClick(int r, int c) {
        if (gameOver || board[r][c].revealed)
            return;

        if (!minesGenerated) {
            generateMines(r, c);
        }

        reveal(r, c);

        if (board[r][c].mine) {
            setBombAppearance(buttons[r][c]);
            showAllMines();
            JOptionPane.showMessageDialog(this, "Game Over!");
            gameOver = true;
            dispose();
            System.exit(0);
        } else if (checkWin()) {
            showAllMines();
            JOptionPane.showMessageDialog(this, "You Win!");
            gameOver = true;
            dispose();
            System.exit(0);
        }
    }

    private void reveal(int r, int c) {
        if (!inBounds(r, c) || board[r][c].revealed)
            return;

        board[r][c].revealed = true;
        JButton btn = buttons[r][c];
        btn.setEnabled(false);
        int count = board[r][c].adjacentMines;

        if (count > 0) {
            btn.setText(String.valueOf(count));
        } else {
            btn.setText("");
            for (int dr = -1; dr <= 1; dr++)
                for (int dc = -1; dc <= 1; dc++)
                    if (dr != 0 || dc != 0)
                        reveal(r + dr, c + dc);
        }
    }

    private void showAllMines() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (board[r][c].mine)
                    setBombAppearance(buttons[r][c]);
    }

    private void setBombAppearance(JButton btn) {
        if (bombIcon != null) {
            btn.setIcon(bombIcon);
            btn.setDisabledIcon(bombIcon);
        }
        btn.setText("");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setEnabled(false);
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }

    private boolean checkWin() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (!board[r][c].mine && !board[r][c].revealed)
                    return false;
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Minesweeper::new);
    }
}
