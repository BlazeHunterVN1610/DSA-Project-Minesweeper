package main;

import javax.swing.*;
import java.awt.*;

public class MinesweeperGame {
    public static void main(String[] args) throws Exception {
        // Load the icon image (assuming it's located at "src/assets/textures/ms_logo.png")
        Image icon = Toolkit.getDefaultToolkit().getImage("src/assets/textures/ms_logo.png");

        // Set the icon before showing the menu
        Menu menu = new Menu();
        menu.setIconImage(icon);
    }
}
