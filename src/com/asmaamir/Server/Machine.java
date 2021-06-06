package com.asmaamir.Server;

import java.util.Arrays;

public class Machine {
    private String name;
    private String ID;
    private String type;
    private String speed;

    public Machine(String data) {
        String[] split = data.split(";");
        Arrays.sort(split);
        //  id name speed type
        System.out.println(data);
        setID(split[0].split("\\?")[1]);
        setName(split[1].split("\\?")[1]);
        setSpeed(split[2].split("\\?")[1]);
        setType(split[3].split("\\?")[1]);

    }

    public Machine(String name, String ID, String type, String speed) {
        this.name = name;
        this.type = type;
        this.speed = speed;
        this.ID = ID;
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

    @Override
    public String toString() {
        return String.format("name?" + getName() + ";ID?" + getID() + ";type?" + getType() + ";speed?" + getSpeed());
    }

}

