package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static int port;
    private static Dictionary dictionary;
    public static int socketCount;

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

        ServerSocket listener = new ServerSocket(port);
        System.out.println("Server Printing: Hangman Server is Running on port " + port);
        socketCount = 0;
        while (true) {
            Socket socket;
            if (socketCount < 3) {
                socketCount++;
                socket = listener.accept();
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
            } else { // already 3 connected clients, send connection denial message
                socket = listener.accept();
                System.out.println("Maximum of 3 connections. Connection request from " + socket.toString() + " denied.");
                char[] packet = Util.encodeMessagePacket(MessagePacket.ERROR_TOO_MANY_CLIENTS);
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(String.valueOf(packet));
                socket.close();
            }
        }
    }
}
