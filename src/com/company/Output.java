package com.company;
import javax.swing.*;
public class Output {
    public static void println(String msg){
        System.out.println(msg);
    }

    public static void print(String msg){
        System.out.print(msg);
    }

    public static void popWindows(String strWarning, String strTitle) {
        JOptionPane.showMessageDialog(null,strWarning, strTitle,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
