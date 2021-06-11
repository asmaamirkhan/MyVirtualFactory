package com.asmaamir.Planner.entities;

public class Order {
    String id;
    String type;
    String quantity;

    public Order(String data) {
        String[] split = data.split(";");
        //Arrays.sort(split);
        //  duration id type
        /*System.out.println(data);
        this.quantity = split[0].split("\\?")[1];
        this.id = split[1].split("\\?")[1];
        this.type = split[2].split("\\?")[1];*/
        for (String part : split) {
            String[] pair = part.split("\\?");
            if (pair[0].equals("type"))
                this.type = pair[1];
            else if (pair[0].equals("ID"))
                this.id = pair[1];
            else if (pair[0].equals("duration"))
                this.quantity = pair[1];
        }
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        String result = "";
        result += "ID: " + getId();
        result += ", Type: " + getType();
        result += ", Quantity: " + getQuantity();
        result += "\n";
        return result;
    }
}
