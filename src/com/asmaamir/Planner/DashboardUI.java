package com.asmaamir.Planner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardUI extends JFrame {
    // Components of the Form
    private final Container c;
    private FormObserver observer;

    // constructor, to initialize the components
    // with default values.
    public DashboardUI() {
        setTitle("Planner Login Form");
        setBounds(300, 90, 550, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        JLabel title = new JLabel("Planner Dashboard");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setSize(500, 30);
        title.setLocation(100, 30);
        c.add(title);


        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void setFormObserver(FormObserver observer) {
        this.observer = observer;
    }

    public interface FormObserver {
        public void onSubmit(String name, String password);
    }
}
