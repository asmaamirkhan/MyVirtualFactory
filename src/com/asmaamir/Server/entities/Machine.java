package com.asmaamir.Server.entities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Machine {
    public MachineObserver observer;
    private String name;
    private String ID;
    private String type;
    private String speed;
    private boolean isBusy;


    public Machine(String data) {
        String[] split = data.split(";");
        Arrays.sort(split);
        //  id name speed type
        System.out.println(data);
        setID(split[0].split("\\?")[1]);
        setName(split[1].split("\\?")[1]);
        setSpeed(split[2].split("\\?")[1]);
        setType(split[3].split("\\?")[1]);
        this.isBusy = false;
    }

    public Machine(String name, String ID, String type, String speed) {
        this.name = name;
        this.type = type;
        this.speed = speed;
        this.ID = ID;
        this.isBusy = false;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    // duration by seconds
    public void setBusyForWhile(double duration) {
        this.isBusy = true;
        int delay = (int) duration * 1000;
        System.out.println("Order is assigned: " + duration + " sec");
        observer.onSetOrder(getID());
        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setBusy(false);
                System.out.println("Order is done");
                observer.onOrderDone(getID());
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public MachineObserver getObserver() {
        return observer;
    }

    public void setObserver(MachineObserver observer) {
        this.observer = observer;
    }

    @Override
    public String toString() {
        return String.format("name?" + getName() + ";ID?" + getID() + ";type?" + getType() + ";speed?" + getSpeed());
    }

    public interface MachineObserver {
        public void onOrderDone(String id);

        public void onSetOrder(String id);
    }
}

