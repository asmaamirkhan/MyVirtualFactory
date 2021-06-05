package com.asmaamir.Planner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PlannerClient {
    private static final int PORT = 1234;
    private static final int CLIENT_TYPE = 2;
    private static InetAddress host;
    private String name;
    private int password;

    public PlannerClient() {
        getUserInfo();
        connect2Server();
    }

    public static void sendMessages() {
        Socket socket = null;

        try {
            String message, response;
            socket = new Socket(host, PORT);

            Scanner networkInput = new Scanner(socket.getInputStream());
            PrintWriter networkOutput = new PrintWriter(socket.getOutputStream(), true);

            message = String.valueOf(CLIENT_TYPE);
            networkOutput.println(message);
            response = networkInput.nextLine();
            System.out.println("\nSERVER> " + response);
            if (response.equals("OK")) {//Set up stream for keyboard entry...
                Scanner userEntry = new Scanner(System.in);
                do {
                    System.out.print("Enter message ('QUIT' to exit): ");
                    message = userEntry.nextLine();
                    networkOutput.println(message);
                    response = networkInput.nextLine();
                    System.out.println("\nSERVER> " + response);
                } while (!message.equals("QUIT"));
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            try {
                System.out.println("\nClosing connection...");
                socket.close();
            } catch (IOException ioEx) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }

    private void connect2Server() {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("\nHost ID not found!\n");
            System.exit(1);
        }
    }

    private void getUserInfo() {
        String answer;
        Scanner machineScanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        answer = machineScanner.nextLine();
        this.name = answer;

        System.out.println("Enter password: ");
        answer = machineScanner.nextLine();
        this.name = answer;
    }


}
