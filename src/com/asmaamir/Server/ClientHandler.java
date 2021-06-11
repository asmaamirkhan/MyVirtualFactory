package com.asmaamir.Server;

import com.asmaamir.Server.entities.Machine;
import com.asmaamir.Server.entities.Order;
import com.asmaamir.Server.entities.User;

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
    private static ArrayList<User> onlineUsers = new ArrayList<>();
    private String SPLITTER = ",";
    private Socket client;
    private Scanner input;
    private PrintWriter output;
    private boolean isClientAlive = true;
    private int type;
    private int aliveClients = 0;
    private String ID;

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
            //System.out.println(machine.toString());
            result += machine.toString() + "&";
        }
        if (result.length() > 0)
            result = result.substring(0, result.length() - 1);
        return result;
    }


    public static String getAliveMachineIDs() {
        String result = "";
        for (Machine machine : aliveMachines) {
            //System.out.println(machine.toString());
            result += machine.getID() + "&";
        }
        if (result.length() > 0)
            result = result.substring(0, result.length() - 1);
        return result;
    }

    public static String getWaitingOrders() {
        String result = "";
        for (Order order : activeOrders) {
            //System.out.println(order.toString());
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
        ID = machine.getID();
        machine.setObserver(new Machine.MachineObserver() {
            @Override
            public void onOrderDone(String id) {
                output.println(constructRequest("finishOrder"));
                assignOrder();
            }

            @Override
            public void onSetOrder(String id) {
                output.println(constructRequest("assignOrder"));
            }
        });
        assignOrder();
    }

    public void registerOrder(String data) {
        Order order = new Order(data);
        activeOrders.add(order);
    }

    public void assignOrder() {
        List<Order> ordersToRemove = new ArrayList<Order>();
        List<Machine> machinesToRemove = new ArrayList<Machine>();
        for (Machine machine : aliveMachines) {
            if (!machine.isBusy()) {
                for (Order order : activeOrders) {
                    if (ordersToRemove.contains(order) || machine.isBusy())
                        continue;
                    if (order.getType().equals(machine.getType())) {
                        double quantity = Double.parseDouble(order.getDuration());
                        double speed = Double.parseDouble(machine.getSpeed());
                        double duration = quantity / speed; // minute
                        machine.setBusyForWhile(duration * 60);
                        ordersToRemove.add(order);
                        machinesToRemove.add(machine);
                        break;
                    }
                }
            }
        }
        activeOrders.removeAll(ordersToRemove);
        //aliveMachines.removeAll(machinesToRemove);
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

    public boolean logUser(String data) {
        String name = data.split(";")[0].split("\\?")[1];
        for (User user : onlineUsers) {
            if (user.getName().equals(name)) {
                return false;
            }
        }
        User user = new User(name);
        onlineUsers.add(user);
        ID = name;
        return true;
    }

    public void removeUser() {
        List<User> toRemove = new ArrayList<User>();
        for (User user : onlineUsers) {
            if (user.getName().equals(ID)) {
                toRemove.add(user);
                break;
            }
        }
        onlineUsers.removeAll(toRemove);
    }

    public void removeMachine() {
        List<Machine> toRemove = new ArrayList<Machine>();

        for (Machine machine : aliveMachines) {
            if (machine.getID().equals(ID)) {
                toRemove.add(machine);
                break;
            }
        }
        aliveMachines.removeAll(toRemove);
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
                        output.println(constructResponse(200, "" + clientType));
                        registerMachine(rawData);
                    } else if (opCode.equals("disconnect")) {
                        removeMachine();
                        closeConnection();
                    }
                } else if (clientType == 2) { //planner
                    if (opCode.equals("login")) {
                        String rawData = getData(received);
                        if (verifyUser(rawData)) {
                            if (logUser(rawData)) {
                                output.println(constructResponse(200, "null"));
                            } else {
                                output.println(constructResponse(402, "user has already logged in"));
                                closeConnection();
                            }
                        } else {
                            output.println(constructResponse(401, "wrong pass"));
                            closeConnection();
                        }
                    } else if (opCode.equals("getAliveMachineIDs")) {
                        output.println(constructResponse(200, getAliveMachineIDs()));
                    } else if (opCode.equals("getAliveMachines")) {
                        output.println(constructResponse(200, getAliveMachines()));
                    } else if (opCode.equals("getInfoByMachineID")) {
                        String id = getData(received);
                        String result = getMachineByID(id);
                        output.println(constructResponse(200, result));
                    } else if (opCode.equals("getWaitingOrders")) {
                        String result = getWaitingOrders();
                        output.println(constructResponse(200, result));
                    } else if (opCode.equals("setNewOrder")) {
                        String rawData = getData(received);
                        registerOrder(rawData);
                        assignOrder();
                        output.println(constructResponse(200, "order done"));
                    } else if (opCode.equals("disconnect")) {
                        removeUser();
                        closeConnection();
                    }

                }
                //output.println(constructResponse(200, "" + clientType));

            } else {
                output.println(constructResponse(404, null));
            }

            //Repeat above until 'QUIT' sent by client...
        } while (isClientAlive);


    }

    public void closeConnection() {
        isClientAlive = false;
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
