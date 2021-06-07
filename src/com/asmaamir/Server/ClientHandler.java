package com.asmaamir.Server;

import com.asmaamir.Server.entities.Machine;
import com.asmaamir.Server.entities.Order;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class ClientHandler extends Thread {
    private static ArrayList<Machine> aliveMachines = new ArrayList<>();
    private static ArrayList<Order> activeOrders = new ArrayList<>();
    private String SPLITTER = ",";
    private Socket client;
    private Scanner input;
    private PrintWriter output;
    private int type;
    private int aliveClients = 0;

    public ClientHandler(Socket socket) {
        //Set up reference to associated socket...
        client = socket;
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }


    public static String getAliveMachines() {
        String result = "";
        for (Machine machine : aliveMachines) {
            System.out.println(machine.toString());
            result += machine.toString() + "&";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    public static String getAliveMachineIDs() {
        String result = "";
        for (Machine machine : aliveMachines) {
            System.out.println(machine.toString());
            result += machine.getID() + "&";
        }
        if (result.length() > 0)
            result = result.substring(0, result.length() - 1);
        return result;
    }

    public static String getWaitingOrders() {
        String result = "";
        for (Order order : activeOrders) {
            System.out.println(order.toString());
            result += order.toString() + "&";
        }
        if (result.length() > 0)
            result = result.substring(0, result.length() - 1);
        return result;
    }

    public static String getMachineByID(String id) {
        for (Machine machine : aliveMachines) {
            if (machine.getID().equals(id)) {
                return machine.toString();
            }
        }
        return "";
    }

    public void registerMachine(String data) {
        Machine machine = new Machine(data);
        aliveMachines.add(machine);
        machine.setObserver(new Machine.MachineObserver() {
            @Override
            public void onOrderDone(String id) {
                output.println(constructRequest("finishOrder"));
            }

            @Override
            public void onSetOrder(String id) {
                output.println(constructRequest("assignOrder"));
            }
        });
    }

    public void registerOrder(String data) {
        Order order = new Order(data);
        activeOrders.add(order);
    }

    public void assignOrder() {
        System.out.println("machines: " + aliveMachines.size() + " orders: " + activeOrders.size());
        List<Order> toRemove = new ArrayList<Order>();
        for (Machine machine : aliveMachines) {
            if (!machine.isBusy()) {
                for (Order order : activeOrders) {
                    if (order.getType().equals(machine.getType())) {
                        double quantity = Double.parseDouble(order.getDuration());
                        double speed = Double.parseDouble(machine.getSpeed());
                        double duration = quantity / speed; // minute
                        machine.setBusyForWhile(duration * 60);
                        //activeOrders.remove(order);
                        toRemove.add(order);

                        //output.println(constructRequest("assignOrder"));
                    }
                }
            }
        }
        activeOrders.removeAll(toRemove);
        System.out.println("machines: " + aliveMachines.size() + " orders: " + activeOrders.size());
    }

    public void parseMessage(String message) {
        String[] parts = message.split(SPLITTER);
        for (int i = 0; i < parts.length; i++) {
            System.out.println(parts[i]);
        }
    }

    public boolean isValidMessage(String message) {
        String[] parts = message.split(SPLITTER);
        if (parts.length != 3)
            return false;
        Arrays.sort(parts);
        if (!parts[0].startsWith("data:"))
            return false;
        if (!parts[1].startsWith("opCode:"))
            return false;
        if (!parts[2].startsWith("type:"))
            return false;
        return true;
    }

    public String constructResponse(int code, String data) {
        String res = "code:" + code;
        res += ",data:" + data;
        return res;
    }

    public String constructRequest(String opCode) {
        String res = "opCode:" + opCode;
        return res;
    }

    public int getClientType(String message) {
        String[] parts = message.split(SPLITTER);
        Arrays.sort(parts);
        String[] data = parts[2].split(":");
        return Integer.parseInt(data[1]);
    }

    public String getOpcode(String message) {
        String[] parts = message.split(SPLITTER);
        Arrays.sort(parts);
        String[] data = parts[1].split(":");
        return data[1];
    }

    public String getData(String message) {
        String[] parts = message.split(SPLITTER);
        Arrays.sort(parts);
        String[] data = parts[0].split(":");
        return data[1];
    }

    public boolean verifyUser(String data) {
        String name = data.split(";")[0].split("\\?")[1];
        String password = data.split(";")[1].split("\\?")[1];
        try {
            URL path = ClientHandler.class.getResource("admins.txt");
            File file = new File(path.getFile());
            Scanner input = null;
            input = new Scanner(file);

            while (input.hasNextLine()) {
                String[] line = input.nextLine().split(";");
                if (line[0].equals(name) && line[1].equals(password)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void run() {
        String received;
        do {
            received = input.nextLine();
            if (isValidMessage(received)) {
                //parseMessage(received);
                int clientType = getClientType(received);
                String opCode = getOpcode(received);
                if (clientType == 1) { // machine
                    if (opCode.equals("register")) {
                        String rawData = getData(received);
                        registerMachine(rawData);
                        output.println(constructResponse(200, "" + clientType));
                    }
                } else if (clientType == 2) { //planner
                    if (opCode.equals("login")) {
                        String rawData = getData(received);
                        System.out.println("Auth: " + verifyUser(rawData));
                        if (verifyUser(rawData)) {
                            output.println(constructResponse(200, "" + getAliveMachineIDs()));
                        } else {
                            output.println(constructResponse(401, "" + clientType));
                        }
                    } else if (opCode.equals("getAliveMachines")) {
                        System.out.println(constructResponse(200, getAliveMachines()));
                        output.println(constructResponse(200, getAliveMachines()));
                    } else if (opCode.equals("getInfoByMachineID")) {
                        String id = getData(received);
                        String result = getMachineByID(id);
                        output.println(constructResponse(200, result));
                    } else if (opCode.equals("getWaitingOrders")) {
                        String result = getWaitingOrders();
                        output.println(constructResponse(200, result));
                    } else if (opCode.equals("setNewOrder")) {
                        System.out.println(received);
                        String rawData = getData(received);
                        System.out.println(rawData);
                        registerOrder(rawData);
                        assignOrder();
                        output.println(constructResponse(200, "order done"));
                    }

                }
                //output.println(constructResponse(200, "" + clientType));

            } else {
                output.println(constructResponse(404, null));
            }

            //Repeat above until 'QUIT' sent by client...
        } while (!received.equals("QUIT"));

        try {
            if (client != null) {
                System.out.println("Closing down connection...");
                client.close();
            }
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
        }
    }
}
