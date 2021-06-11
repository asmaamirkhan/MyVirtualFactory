package com.asmaamir.Planner;

import com.asmaamir.Planner.entities.Machine;
import com.asmaamir.Planner.entities.Order;
import com.asmaamir.Planner.ui.DashboardUI;
import com.asmaamir.Planner.ui.LoginForm;
import com.asmaamir.Planner.ui.OrderForm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class PlannerClient {
    private static final int PORT = 1234;
    private static final int CLIENT_TYPE = 2;
    private static InetAddress host;
    private static String types[] = {"CNC", "DOKUM", "KILIF", "KAPLAMA"};
    Socket socket = null;
    private String name;
    private String password;
    private LoginForm form;

    public PlannerClient() {
        form = new LoginForm();
        form.setFormObserver(new LoginForm.FormObserver() {
            @Override
            public void onSubmit(String name, String password) {
                System.out.println(name + " " + password);
                initClient(name, password);
                connect2Server();

            }
        });
    }

    private void initClient(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String constructLoginMessage() {
        String msg = "type:" + CLIENT_TYPE;
        msg += ",opCode:" + "login";
        msg += ",data:";
        msg += "name?" + name;
        msg += ";password?" + password;
        return msg;
    }

    public String constructMessage(String action) {
        String msg = "type:" + CLIENT_TYPE;
        msg += ",opCode:" + action;
        msg += ",data:" + "null";
        return msg;
    }

    public String constructMessage(String action, String data) {
        String msg = "type:" + CLIENT_TYPE;
        msg += ",opCode:" + action;
        msg += ",data:" + data;
        return msg;
    }

    public void connect2Server() {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("\nHost ID not found!\n");
            System.exit(1);
        }
        try {
            String message, response;
            System.out.println("Connecting to server..");
            socket = new Socket(host, PORT);
            Scanner networkInput = new Scanner(socket.getInputStream());
            PrintWriter networkOutput = new PrintWriter(socket.getOutputStream(), true);
            message = constructLoginMessage();
            networkOutput.println(message);
            response = networkInput.nextLine();
            //System.out.println("\nSERVER> " + response);
            if (response.startsWith("code:200")) {//Set up stream for keyboard entry...
                DashboardUI ui = new DashboardUI();
                networkOutput.println(constructMessage("getAliveMachineIDs"));
                response = networkInput.nextLine();

                if (response.startsWith("code:200") && response.split(",")[1].split(":").length > 1) {
                    String[] aliveIDs = response.split(",")[1].split(":")[1].split("&");
                    ui.setAliveIDs(aliveIDs);
                    //System.out.println(response.split(",")[1]);
                }


                ui.setFormObserver(new DashboardUI.FormObserver() {
                    @Override
                    public void onGetAliveMachinesClicked() {
                        networkOutput.println(constructMessage("getAliveMachines"));
                        String response = networkInput.nextLine();
                        System.out.println(response);
                        if (response.split(",")[1].split(":").length > 1) {
                            String[] result = response.split(",")[1].split(":")[1].split("&");
                            String output = "";
                            ArrayList<Machine> machines = new ArrayList<>();
                            for (String str : result) {
                                System.out.println(str);
                                Machine machine = new Machine(str);
                                machines.add(machine);
                            }
                            for (String type : types) {
                                output += type + ":\n";
                                for (Machine machine : machines) {
                                    System.out.println(machine.getType() + "  " + type);
                                    if (machine.getType().equals(type)) {
                                        output += machine.toString();
                                    }
                                }
                                output += "\n------------\n";
                            }

                            ui.setTextAreaContent(output);
                        } else {
                            ui.setTextAreaContent("No active machine");
                        }
                    }

                    @Override
                    public void onRefreshIDs() {
                        networkOutput.println(constructMessage("getAliveMachineIDs"));
                        String response = networkInput.nextLine();

                        if (response.startsWith("code:200") && response.split(",")[1].split(":").length > 1) {
                            String[] aliveIDs = response.split(",")[1].split(":")[1].split("&");
                            ui.setAliveIDs(aliveIDs);
                            System.out.println(response.split(",")[1]);
                        }
                    }

                    @Override
                    public void onGetInfoByMachineClicked(String id) {
                        networkOutput.println(constructMessage("getInfoByMachineID", id));
                        String response = networkInput.nextLine();
                        if (response.split(",")[1].split(":").length > 1) {
                            String[] result = response.split(",")[1].split(":")[1].split("&");
                            String output = "";
                            for (String str : result) {
                                System.out.println(str);
                                String[] temp = str.split(";");
                                for (String s : temp) {
                                    output += s.split("\\?")[0] + ": ";
                                    output += s.split("\\?")[1];
                                    output += "\n";
                                }
                                output += "------------\n";
                            }
                            System.out.println("res:" + output);

                            ui.setTextAreaContent(output);
                        } else {
                            ui.setTextAreaContent("ID is invalid");
                        }
                    }

                    @Override
                    public void onGetWaitingOrdersClicked() {
                        //networkOutput.println(constructMessage("getWaitingOrders"));
                        //System.out.println("res:" + networkInput.nextLine());
                        networkOutput.println(constructMessage("getWaitingOrders"));
                        String response = networkInput.nextLine();
                        System.out.println(response);
                        if (response.split(",")[1].split(":").length > 1) {
                            String[] result = response.split(",")[1].split(":")[1].split("&");
                            String output = "";
                            ArrayList<Order> orders = new ArrayList<>();
                            for (String str : result) {
                                System.out.println(str);
                                Order order = new Order(str);
                                orders.add(order);
                            }
                            for (String type : types) {
                                output += type + ":\n";
                                for (Order order : orders) {
                                    if (order.getType().equals(type)) {
                                        output += order.toString();
                                    }
                                }
                                output += "\n------------\n";
                            }

                            ui.setTextAreaContent(output);
                        } else {
                            ui.setTextAreaContent("No waiting order 🎉!");
                        }
                    }

                    @Override
                    public void onSetNewOrderClicked() {
                        OrderForm orderForm = new OrderForm();
                        //openForm();
                        //System.out.println("res:" + networkInput.nextLine());
                        orderForm.setFormObserver(new OrderForm.FormObserver() {
                            @Override
                            public void onSubmit(String id, String type, String duration) {
                                String data = "id?" + id;
                                data += ";type?" + type;
                                data += ";duration?" + duration;
                                networkOutput.println(constructMessage("setNewOrder", data));
                                // TODO: parse response
                                String response = networkInput.nextLine();
                                orderForm.closeForm();
                            }
                        });
                    }

                    @Override
                    public void onDisconnectClicked() {
                        networkOutput.println(constructMessage("disconnect"));
                        closeSocket();
                    }
                });
                // Scanner userEntry = new Scanner(System.in);
               /* do {
                    //System.out.print("Enter message ('QUIT' to exit): ");
                    //message = userEntry.nextLine();
                    //networkOutput.println(message);
                    //response = networkInput.nextLine();
                    //System.out.println("\nSERVER> " + response);
                } while (!message.equals("QUIT"));*/
            } else if (response.startsWith("code:401")) {
                form.setResult("Incorrect username or password");
            } else if (response.startsWith("code:402")) {
                form.setResult("User has already logged in");
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    private void openForm() {
        OrderForm orderForm = new OrderForm();
    }

    private void closeSocket() {
        try {
            System.out.println("\nClosing connection...");
            socket.close();
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
            System.exit(1);
        }

    }


}
