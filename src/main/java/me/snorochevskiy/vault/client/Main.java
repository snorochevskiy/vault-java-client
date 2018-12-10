package me.snorochevskiy.vault.client;

import me.snorochevskiy.vault.client.ui.MainWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            MainWindow mw = new MainWindow();
            //mw.pack();
            mw.setVisible(true);
        });
    }

}
