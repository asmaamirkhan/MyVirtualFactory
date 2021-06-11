package com.asmaamir.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


class MachineForm
        extends JFrame
        implements ActionListener {

    private final JTextField tfName;
    // Components of the Form
    private final Container c;
    private final JTextField tfID;
    private final JComboBox cbType;
    private final JTextField tfSpeed;
    private final JButton sub;
    private final JButton reset;
    private final JButton close;
    private final JLabel resLive;
    private final JLabel statusLive;
    private String types[] = {"CNC", "DOKUM", "KILIF", "KAPLAMA"};
    private FormObserver observer;

    // constructor, to initialize the components
    // with default values.
    public MachineForm() {
        setTitle("Machine Type Client Form");
        setBounds(300, 90, 550, 500);
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

        JLabel speed = new JLabel("Speed");
        speed.setFont(new Font("Arial", Font.PLAIN, 20));
        speed.setSize(100, 20);
        speed.setLocation(100, 250);
        c.add(speed);

        tfSpeed = new JTextField();
        tfSpeed.setFont(new Font("Arial", Font.PLAIN, 15));
        tfSpeed.setSize(200, 20);
        tfSpeed.setLocation(200, 250);
        c.add(tfSpeed);

        JLabel status = new JLabel("Status");
        status.setFont(new Font("Arial", Font.PLAIN, 10));
        status.setSize(100, 20);
        status.setLocation(100, 300);
        c.add(status);

        statusLive = new JLabel("EMPTY");
        statusLive.setFont(new Font("Arial", Font.PLAIN, 10));
        statusLive.setSize(200, 20);
        statusLive.setLocation(200, 300);
        c.add(statusLive);

        JLabel res = new JLabel("Connection");
        res.setFont(new Font("Arial", Font.PLAIN, 10));
        res.setSize(100, 20);
        res.setLocation(100, 350);
        c.add(res);

        resLive = new JLabel("Not connected");
        resLive.setFont(new Font("Arial", Font.PLAIN, 10));
        resLive.setSize(200, 20);
        resLive.setLocation(200, 350);
        c.add(resLive);


        sub = new JButton("Submit");
        sub.setFont(new Font("Arial", Font.PLAIN, 15));
        sub.setSize(100, 20);
        sub.setLocation(50, 400);
        sub.addActionListener(this);
        c.add(sub);

        reset = new JButton("Reset");
        reset.setFont(new Font("Arial", Font.PLAIN, 15));
        reset.setSize(100, 20);
        reset.setLocation(170, 400);
        reset.addActionListener(this);
        c.add(reset);

        close = new JButton("Disconnect");
        close.setFont(new Font("Arial", Font.PLAIN, 15));
        close.setSize(150, 20);
        close.setLocation(290, 400);
        close.addActionListener(this);
        c.add(close);

        setVisible(true);
    }

    // method actionPerformed()
    // to get the action performed
    // by the user and act accordingly
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sub) {

            resLive.setText("Connecting..");
            observer.onSubmit(tfName.getText(), tfID.getText(), "" + cbType.getSelectedItem(), tfSpeed.getText());
        } else if (e.getSource() == reset) {
            String def = "";
            tfName.setText(def);
            tfID.setText(def);
            tfSpeed.setText(def);
            resLive.setText("Not connected");
            cbType.setSelectedIndex(0);
        } else if (e.getSource() == close) {
            resLive.setText("Not connected");
            try {
                observer.onClose();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void setConnectionStatus(String status) {
        resLive.setText(status);
    }

    public void setFormObserver(FormObserver observer) {
        this.observer = observer;
    }

    public void setBusinessStatus(boolean isBusy) {
        if (isBusy) {
            statusLive.setText("BUSY");
        } else {
            statusLive.setText("EMPTY");
        }
    }

    public interface FormObserver {
        public void onSubmit(String name, String ID, String type, String speed);

        public void onClose() throws IOException;
    }
}
