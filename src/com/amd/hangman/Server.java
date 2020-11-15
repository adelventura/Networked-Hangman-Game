package com.amd.hangman;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static int port;
    private static Dictionary dictionary;

    // port [file]
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            // incorrect num args
            System.out.println("Incorrect arguments. Please provide a port number.");
        }

        port = Integer.parseInt(args[0]);

        if (args.length == 2) {
            // use file for dictionary
            dictionary = Dictionary.fromFile(args[1]);
        } else {
            // use default dictionary
            dictionary = Dictionary.defaultDictionary();
        }

        ServerSocket listener = new ServerSocket(port);
        System.out.println("Hangman Server is Running on port " + port);
        while (true) {
            Socket socket = listener.accept();
            System.out.println("Received connection: " + socket.toString());

            GameConfig config = new GameConfig(
                    dictionary,
                    6
            );
            Thread gameThread = new Game(
                    config,
                    socket,
                    new BufferedReader(new InputStreamReader(socket.getInputStream())),
                    new DataOutputStream(socket.getOutputStream())
            );
            gameThread.start();
        }
    }
}
