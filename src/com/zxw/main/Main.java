package com.zxw.main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            //UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MainJPanel.createGUIAndShow();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

