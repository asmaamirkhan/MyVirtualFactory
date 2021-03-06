package com.asmaamir.Planner.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardUI extends JFrame implements ActionListener {
    // Components of the Form
    private final Container c;
    private final JButton getAliveMachines;
    private final JButton getInfoByMachine;
    private final JButton getWaitingOrders;
    private final JButton setNewOrder;
    private final JButton disconnect;
    private final JButton refreshIDs;
    private final JTextArea tout;
    private JComboBox cbAliveIDs;
    private FormObserver observer;


    // constructor, to initialize the components
    // with default values.
    public DashboardUI() {
        setTitle("Dashboard");
        setBounds(300, 90, 800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        JLabel title = new JLabel("Planner Dashboard");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setSize(500, 30);
        title.setLocation(200, 30);
        c.add(title);

        getAliveMachines = new JButton("List alive machines");
        getAliveMachines.setFont(new Font("Arial", Font.PLAIN, 15));
        getAliveMachines.setSize(200, 20);
        getAliveMachines.setLocation(50, 100);
        getAliveMachines.addActionListener(this);
        c.add(getAliveMachines);

        refreshIDs = new JButton("Refresh IDs");
        refreshIDs.setFont(new Font("Arial", Font.PLAIN, 15));
        refreshIDs.setSize(200, 20);
        refreshIDs.setLocation(50, 130);
        refreshIDs.addActionListener(this);
        c.add(refreshIDs);


        getInfoByMachine = new JButton("Get Machine by ID");
        getInfoByMachine.setFont(new Font("Arial", Font.PLAIN, 15));
        getInfoByMachine.setSize(200, 20);
        getInfoByMachine.setLocation(50, 160);
        getInfoByMachine.addActionListener(this);
        c.add(getInfoByMachine);


        cbAliveIDs = new JComboBox();
        cbAliveIDs.setFont(new Font("Arial", Font.PLAIN, 15));
        cbAliveIDs.setSize(50, 20);
        cbAliveIDs.setLocation(250, 160);
        c.add(cbAliveIDs);

        getWaitingOrders = new JButton("List waiting orders");
        getWaitingOrders.setFont(new Font("Arial", Font.PLAIN, 15));
        getWaitingOrders.setSize(200, 20);
        getWaitingOrders.setLocation(50, 190);
        getWaitingOrders.addActionListener(this);
        c.add(getWaitingOrders);

        setNewOrder = new JButton("Give new order");
        setNewOrder.setFont(new Font("Arial", Font.PLAIN, 15));
        setNewOrder.setSize(200, 20);
        setNewOrder.setLocation(50, 220);
        setNewOrder.addActionListener(this);
        c.add(setNewOrder);

        disconnect = new JButton("Disconnect");
        disconnect.setFont(new Font("Arial", Font.PLAIN, 15));
        disconnect.setSize(200, 20);
        disconnect.setLocation(50, 250);
        disconnect.addActionListener(this);
        c.add(disconnect);

        tout = new JTextArea();
        tout.setFont(new Font("Arial", Font.PLAIN, 15));
        tout.setSize(400, 450);
        tout.setLocation(300, 100);
        tout.setLineWrap(true);
        tout.setEditable(false);
        tout.setWrapStyleWord(true);
        //JScrollPane scroll = new JScrollPane(tout);
        //scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        c.add(tout);
        //tout.setAutoscrolls(true);
        //c.add(scroll);


        setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getAliveMachines) {
            observer.onGetAliveMachinesClicked();
        } else if (e.getSource() == refreshIDs) {
            observer.onRefreshIDs();

        } else if (e.getSource() == getInfoByMachine) {
            observer.onGetInfoByMachineClicked(String.valueOf(cbAliveIDs.getSelectedItem()));
        } else if (e.getSource() == getWaitingOrders) {
            observer.onGetWaitingOrdersClicked();
        } else if (e.getSource() == setNewOrder) {
            observer.onSetNewOrderClicked();
        } else if (e.getSource() == disconnect) {
            observer.onDisconnectClicked();
        }

    }

    public void setTextAreaContent(String content) {
        tout.setText(content);
    }

    public void setFormObserver(FormObserver observer) {
        this.observer = observer;
    }

    public void setAliveIDs(String[] IDs) {
        cbAliveIDs.removeAllItems();
        for (String id : IDs) {
            cbAliveIDs.addItem(id);
        }
    }

    public interface FormObserver {

        public void onRefreshIDs();

        public void onGetAliveMachinesClicked();

        public void onGetInfoByMachineClicked(String id);

        public void onGetWaitingOrdersClicked();

        public void onSetNewOrderClicked();

        public void onDisconnectClicked();
    }
}
