package com.asmaamir.Server.entities;

public class Machine {
    private final static String VALUE_SPLITTER = "\\?";
    private final static String ARRAY_SPLITTER = ";";
    public MachineObserver observer;
    private String name;
    private String ID;
    private String type;
    private String speed;
    private boolean isBusy;


    public Machine(String data) {
        System.out.println(data);
        String[] split = data.split(ARRAY_SPLITTER);
        for (String part : split) {
            String[] pair = part.split(VALUE_SPLITTER);
            if (pair[0].equals("name"))
                this.name = pair[1];
            else if (pair[0].equals("id"))
                this.ID = pair[1];
            else if (pair[0].equals("type"))
                this.type = pair[1];
            else if (pair[0].equals("speed"))
                this.speed = pair[1];
            else if (pair[0].equals("isBusy"))
                this.setBusy(false);
        }
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
        if (!busy) {
            observer.onOrderDone(getID());
        }
    }

    // duration by seconds
    public void setBusyForWhile(double duration) {
        this.isBusy = true;
        int delay = (int) duration * 1000;
        System.out.println("Machine ID: " + getID() + ", order is assigned: " + duration + " sec");
        observer.onSetOrder(getID(), duration);
        /*Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setBusy(false);
                System.out.println("Machine ID: " + getID() + ", order is done");
                observer.onOrderDone(getID());
            }
        });
        timer.setRepeats(false);
        timer.start();*/
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
        return String.format("name?" + getName() + ";ID?" + getID() + ";type?" + getType() + ";speed?" + getSpeed() + ";isBusy?" + isBusy);
    }

    public interface MachineObserver {
        public void onOrderDone(String id);

        public void onSetOrder(String id, double duration);
    }
}

