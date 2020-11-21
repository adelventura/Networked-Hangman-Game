package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static int socketCount;

    // port [file]
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            // incorrect num args
            System.out.println("Incorrect arguments. Please provide a port number.");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Dictionary dictionary = Dictionary.defaultDictionary();

        if (args.length == 2) {
            // use file for dictionary
            dictionary = Dictionary.fromFile(args[1]);
        }

        startServer(port, dictionary);
    }

    private static void startServer(int port, Dictionary dictionary) throws Exception {
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Hangman Server is Running on port " + port);
        socketCount = 0;
        while (true) {
            Socket socket = listener.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            System.out.println("Received connection: " + socket.toString());
            if (socketCount >= 3) {
                System.out.println("Maximum of 3 connections. Connection request from " + socket.toString() + " denied.");
                output.write(MessagePacket.encode(MessagePacket.ERROR_TOO_MANY_CLIENTS));
                socket.close();
                continue;
            }

            socketCount++;

            GameConfig config = new GameConfig(
                    dictionary,
                    6
            );

            Thread gameThread = new Game(config, socket, input, output);
            gameThread.start();
        }
    }
}
