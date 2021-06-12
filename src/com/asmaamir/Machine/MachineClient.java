package com.asmaamir.Machine;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MachineClient {
    private static final int PORT = 1234;
    private static final int CLIENT_TYPE = 1;
    private static InetAddress host;
    Socket socket = null;
    private MachineForm form;
    private String name, ID, type, speed;
    private boolean isBusy = false;
    private boolean isConnected = false;
    private Thread workerThread;

    public MachineClient() {
        form = new MachineForm();
        form.setFormObserver(new MachineForm.FormObserver() {
            @Override
            public void onSubmit(String name, String ID, String type, String speed) {
                System.out.println("fetch: " + name + " " + ID + " " + type + " " + speed);
                initClient(name, ID, type, speed);
                connect2Server();
            }

            @Override
            public void onClose() throws IOException {
                PrintWriter networkOutput = new PrintWriter(socket.getOutputStream(), true);
                String message = constructMessage("disconnect");
                networkOutput.println(message);
                closeSocket();
            }
        });
    }

    private void initClient(String name, String ID, String type, String speed) {
        this.name = name;
        this.ID = ID;
        this.type = type;
        this.speed = speed;
    }

    public String constructMessage() {
        String msg = "type:" + CLIENT_TYPE;
        msg += ",opCode:" + "register";
        msg += ",data:";
        msg += "id?" + ID;
        msg += ";type?" + type;
        msg += ";speed?" + speed;
        msg += ";name?" + name;
        return msg;
    }

    public String constructMessage(String action) {
        String msg = "type:" + CLIENT_TYPE;
        msg += ",opCode:" + action;
        msg += ",data:" + "null";
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
            message = String.valueOf(constructMessage());
            networkOutput.println(message);
            response = networkInput.nextLine();
            System.out.println(response);
            if (response.startsWith("code:")) {
                form.setConnectionStatus("Connected");
                System.out.println(response);
                isConnected = true;
            }
            workerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (networkInput.hasNextLine()) {
                        System.out.println("conn " + isConnected);
                        String response = networkInput.nextLine();
                        System.out.println(response);
                        if (response.startsWith("opCode")) {
                            if (response.startsWith("opCode:assignOrder")) {
                                form.setBusinessStatus(true);
                            } else if (response.startsWith("opCode:finishOrder")) {
                                form.setBusinessStatus(false);
                            }
                        }
                    }
                }
            });
            workerThread.start();

        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
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
