package com.asmaamir.Server.entities;

import java.util.Arrays;

public class Order {
    String id;
    String type;
    String duration;

    public Order(String data) {
        String[] split = data.split(";");
        Arrays.sort(split);
        //  duration id type
        System.out.println(data);
        setDuration(split[0].split("\\?")[1]);
        setId(split[1].split("\\?")[1]);
        setType(split[2].split("\\?")[1]);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("ID?" + getId() + ";type?" + getType() + ";duration?" + getDuration());
    }
}
