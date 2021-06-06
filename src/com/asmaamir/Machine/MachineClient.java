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

    public MachineClient() {
        form = new MachineForm();
        form.setFormObserver(new MachineForm.FormObserver() {
            @Override
            public void onSubmit(String name, String ID, String type, String speed) {
                System.out.println("fetch: " + name + " " + ID + " " + type + " " + speed);
                connect2Server();
            }

            @Override
            public void onClose() {
                closeSocket();
            }
        });
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
            message = String.valueOf(CLIENT_TYPE);
            networkOutput.println(message);
            response = networkInput.nextLine();
            //System.out.println("\nSERVER> " + response);
            if (response.equals("OK")) {//Set up stream for keyboard entry...
                form.setConnectionStatus("Connected");
                // Scanner userEntry = new Scanner(System.in);
               /* do {
                    //System.out.print("Enter message ('QUIT' to exit): ");
                    //message = userEntry.nextLine();
                    //networkOutput.println(message);
                    //response = networkInput.nextLine();
                    //System.out.println("\nSERVER> " + response);
                } while (!message.equals("QUIT"));*/
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    private void closeSocket() {
        try {
            System.out.println("\nClosing connection...");
            PrintWriter networkOutput = new PrintWriter(socket.getOutputStream(), true);
            networkOutput.println("QUIT");
            socket.close();
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
            System.exit(1);
        }

    }

}
