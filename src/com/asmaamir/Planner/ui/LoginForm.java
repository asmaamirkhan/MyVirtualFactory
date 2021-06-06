package com.asmaamir.Planner.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame
        implements ActionListener {
    // Components of the Form
    private final JTextField tfName;
    private final Container c;
    private final JTextField tfPass;
    private final JButton login;
    private JLabel result;
    private FormObserver observer;

    // constructor, to initialize the components
    // with default values.
    public LoginForm() {
        setTitle("Planner Login Form");
        setBounds(300, 90, 550, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        JLabel title = new JLabel("Machine Type Client Form");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setSize(500, 30);
        title.setLocation(100, 30);
        c.add(title);

        JLabel name = new JLabel("Name");
        name.setFont(new Font("Arial", Font.PLAIN, 20));
        name.setSize(100, 20);
        name.setLocation(100, 100);
        c.add(name);

        tfName = new JTextField();
        tfName.setFont(new Font("Arial", Font.PLAIN, 15));
        tfName.setSize(200, 20);
        tfName.setLocation(200, 100);
        c.add(tfName);

        JLabel password = new JLabel("Password");
        password.setFont(new Font("Arial", Font.PLAIN, 20));
        password.setSize(100, 20);
        password.setLocation(100, 200);
        c.add(password);

        tfPass = new JTextField();
        tfPass.setFont(new Font("Arial", Font.PLAIN, 15));
        tfPass.setSize(200, 20);
        tfPass.setLocation(200, 200);
        c.add(tfPass);

        login = new JButton("Login");
        login.setFont(new Font("Arial", Font.PLAIN, 25));
        login.setSize(100, 40);
        login.setLocation(250, 250);
        login.addActionListener(this);
        c.add(login);

        result = new JLabel("");
        result.setFont(new Font("Arial", Font.PLAIN, 10));
        result.setSize(300, 20);
        result.setLocation(100, 300);
        c.add(result);
        setVisible(true);
    }

    public void setResult(boolean res) {
        if (!res) {
            result.setText("Incorrect username or password");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            System.out.println("login");
            observer.onSubmit(tfName.getText(), tfPass.getText());
        }
    }

    public void setFormObserver(FormObserver observer) {
        this.observer = observer;
    }

    public interface FormObserver {
        public void onSubmit(String name, String password);
    }
}
