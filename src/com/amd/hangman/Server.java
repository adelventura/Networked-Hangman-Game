package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static int port;
    private static Dictionary dictionary;

    // port [file]
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            // incorrect num args
            System.out.println("Server Printing: Incorrect arguments. Please provide a port number.");
        }

        port = Integer.parseInt(args[0]);

        if (args.length == 2) {
            // use file for dictionary
            dictionary = Dictionary.fromFile(args[1]);
        } else {
            // use default dictionary
            dictionary = Dictionary.defaultDictionary();
        }

        ServerSocket listener = new ServerSocket(port, 3);
        System.out.println("Server Printing: Hangman Server is Running on port " + port);
        List<Socket> connectionList = new ArrayList<>();

        while (true) {
            Socket socket;
            if (connectionList.size() <= 3) {
                socket = listener.accept();
                connectionList.add(socket);
                System.out.println("Server Printing: Received connection: " + socket.toString());

                GameConfig config = new GameConfig(
                        dictionary,
                        6
                );
                Thread gameThread = new Game(
                        config,
                        socket,
                        new DataInputStream(socket.getInputStream()),
                        new DataOutputStream(socket.getOutputStream())
                );
                gameThread.start();
            } else {
                char[] packet = Util.encodeMessagePacket(Message.ERROR_TOO_MANY_CLIENTS);
            }
        }
    }
}
