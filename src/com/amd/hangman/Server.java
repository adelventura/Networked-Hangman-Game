package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {

    private static String MULTIPLAYER_FLAG = "enable_two_player";
    public static List<GameConfig> games = new Vector<>();

    // port [file] [enable_two_player]
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 3) {
            // incorrect num args
            System.out.println("Incorrect arguments. Please provide a port number.");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Dictionary dictionary = Dictionary.defaultDictionary();
        boolean twoPlayerEnabled = false;

        if (args.length > 1) { // optional args present
            if (args[1].equals(MULTIPLAYER_FLAG)) { // multiplayer mode
                twoPlayerEnabled = true;
            } else {
                // use file for dictionary
                dictionary = Dictionary.fromFile(args[1]);
            }
        }

        if (args.length > 2 && args[2].equals(MULTIPLAYER_FLAG)) {
            twoPlayerEnabled = true;
        }

        startServer(port, dictionary, twoPlayerEnabled);
    }

    private static void startServer(int port, Dictionary dictionary, Boolean twoPlayerEnabled) throws Exception {
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Hangman Server is Running on port " + port);
        while (true) {
            Socket socket = listener.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            System.out.println("Received connection: " + socket.toString());
            if (games.size() >= 3) {
                System.out.println("Maximum of 3 connections. Connection request from " + socket.toString() + " denied.");
                output.write(MessagePacket.encode(MessagePacket.ERROR_TOO_MANY_CLIENTS));
                socket.close();
                continue;
            }

            GameConfig config = new GameConfig(
                    dictionary,
                    6,
                    new GameConfig.Player(
                            socket,
                            input,
                            output
                    )
            );

            if (!twoPlayerEnabled) {
                config.isTwoPlayerGame = false;
                Thread gameThread = new SinglePlayerGame(config);
                gameThread.start();
            } else {
                config.isTwoPlayerGame = true;
                Thread twoPlayerGameStarter = new TwoPlayerGameStarter(config);
                twoPlayerGameStarter.start();
            }
        }
    }
}
