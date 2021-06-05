package com.asmaamir.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerVirtualFactory {
    private static final int PORT = 1234;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Opening port..");
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ioEx) {
            System.out.println("\nUnable to set up port!");
            System.exit(1);
        }

        do {
            //Wait for client...
            Socket client = serverSocket.accept();

            System.out.println("\nNew client accepted.\n");

            //Create a thread to handle communication with
            //this client and pass the constructor for this
            //thread a reference to the relevant socket...
            ClientHandler handler = new ClientHandler(client);

            handler.start();//As usual, this method calls run.
        } while (true);
    }
}

