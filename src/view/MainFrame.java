package view;

import controller.GameController;
import controller.GameLoopThread;
import model.*;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("NO WAY BACK");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);
    }
}
