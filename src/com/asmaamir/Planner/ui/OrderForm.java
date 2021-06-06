package com.asmaamir.Planner.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderForm extends JFrame
        implements ActionListener {

    // Components of the Form
    private final Container c;
    private final JTextField tfID;
    private final JComboBox cbType;
    private final JTextField tfSpeed;
    private final JButton sub;

    private String types[] = {"CNC", "DOKUM", "KILIF", "KAPLAMA"};
    private FormObserver observer;

    public OrderForm() {
        setTitle("New Order Form");
        setBounds(300, 90, 550, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        c = getContentPane();
        c.setLayout(null);

        JLabel title = new JLabel("New Order Form");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setSize(500, 30);
        title.setLocation(100, 30);
        c.add(title);

        JLabel id = new JLabel("ID");
        id.setFont(new Font("Arial", Font.PLAIN, 20));
        id.setSize(100, 20);
        id.setLocation(100, 150);
        c.add(id);

        tfID = new JTextField();
        tfID.setFont(new Font("Arial", Font.PLAIN, 15));
        tfID.setSize(200, 20);
        tfID.setLocation(200, 150);
        c.add(tfID);


        JLabel type = new JLabel("Type");
        type.setFont(new Font("Arial", Font.PLAIN, 20));
        type.setSize(100, 20);
        type.setLocation(100, 200);
        c.add(type);

        cbType = new JComboBox(types);
        cbType.setFont(new Font("Arial", Font.PLAIN, 15));
        cbType.setSize(200, 20);
        cbType.setLocation(200, 200);
        c.add(cbType);

        JLabel speed = new JLabel("Duration");
        speed.setFont(new Font("Arial", Font.PLAIN, 20));
        speed.setSize(100, 20);
        speed.setLocation(100, 250);
        c.add(speed);

        tfSpeed = new JTextField();
        tfSpeed.setFont(new Font("Arial", Font.PLAIN, 15));
        tfSpeed.setSize(200, 20);
        tfSpeed.setLocation(200, 250);
        c.add(tfSpeed);


        sub = new JButton("Submit");
        sub.setFont(new Font("Arial", Font.PLAIN, 15));
        sub.setSize(100, 20);
        sub.setLocation(50, 400);
        sub.addActionListener(this);
        c.add(sub);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sub) {
            observer.onSubmit(tfID.getText(), String.valueOf(cbType.getSelectedItem()), tfSpeed.getText());
        }
    }

    public void closeForm() {
        setVisible(false);
        dispose();
    }

    public void setFormObserver(FormObserver observer) {
        this.observer = observer;
    }

    public interface FormObserver {
        public void onSubmit(String id, String type, String duration);
    }
}
