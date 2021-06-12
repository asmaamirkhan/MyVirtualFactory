package com.asmaamir.Planner.entities;

public class Machine {
    private String name;
    private String ID;
    private String type;
    private String speed;
    private boolean isBusy;


    public Machine(String data) {
        System.out.println(data);
        String[] split = data.split(";");
        for (String part : split) {
            String[] pair = part.split("\\?");
            if (pair[0].equals("name"))
                this.name = pair[1];
            else if (pair[0].equals("ID"))
                this.ID = pair[1];
            else if (pair[0].equals("type"))
                this.type = pair[1];
            else if (pair[0].equals("speed"))
                this.speed = pair[1];
            else if (pair[0].equals("isBusy"))
                this.setIsBusy(pair[1]);
        }
    }

    public Machine(String name, String ID, String type, String speed) {
        this.name = name;
        this.type = type;
        this.speed = speed;
        this.ID = ID;
        this.isBusy = false;
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

    public void setIsBusy(String isBusy) {
        this.isBusy = Boolean.parseBoolean(isBusy);
    }

    @Override
    public String toString() {
        String result = "";
        result += "name: " + getName();
        result += ", ID: " + getID();
        result += ", Type: " + getType();
        result += ", Speed: " + getSpeed();
        result += ", Is Busy: " + isBusy;
        result += "\n";
        return result;
    }

}

