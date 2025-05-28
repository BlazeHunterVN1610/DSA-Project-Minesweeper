package ui;

import javax.swing.*;
import java.awt.*;

public class Button {
    private JButton button;

    public Button(String text, int x, int y, int width, int height) {
        button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBounds(x, y, width, height); // Fixed position
    }

    public JButton getButton() {
        return button;
    }
}
