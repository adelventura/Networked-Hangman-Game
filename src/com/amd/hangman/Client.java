package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static int port;
    static String serverIP;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Scanner scanner;

    public Client(String serverIP, int port) throws Exception {
        socket = new Socket(serverIP, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public static void main(String[] args) throws Exception {
        if (args.length <= 1) {
            System.out.println("Incorrect arguments. Please provide a port number and server IP");
            return;
        }

        serverIP = args[0];
        port = Integer.parseInt(args[1]);
        try {
            Client client = new Client(serverIP, port);
            client.play();
        } catch (ConnectException ce) {
            System.out.println("Could not connect to server.");
            return;
        }
    }

    public void play() throws IOException {
        scanner = new Scanner(System.in);

        // main game loop
        try {
            while (true) {
                byte packetFlag = input.readByte();
                switch (packetFlag) {
                    case 0:
                        ControlPacket packet = ControlPacket.decode(input);
                        System.out.println(packet.formatted());
                        break;
                    default:
                        String message = MessagePacket.decode(packetFlag, input);
                        System.out.print(message);
                        if (hasPrompt(message)) {
                            String response = scanner.nextLine();
                            output.write(MessagePacket.encode(response));
                        }
                        break;
                }
            }
        } catch (EOFException eof) {
            // Server closed the connection, game complete. Fallthrough to exit.
        }
    }

    private boolean hasPrompt(String message) {
        return message.endsWith(": ");
    }
}


