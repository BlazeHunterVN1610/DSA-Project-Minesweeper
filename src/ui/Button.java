package ui;

import javax.swing.*;
import java.awt.*;

public class Button {
    private JButton button;

    public Button(String text, int x, int y, int width, int height) {
        this(text, x, y, width, height, Color.BLACK); // Default text color
    }

    public Button(String text, int x, int y, int width, int height, Color textColor) {
        button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBounds(x, y, width, height);
        button.setForeground(textColor); // Set text color
    }

    public JButton getButton() {
        return button;
    }
}
