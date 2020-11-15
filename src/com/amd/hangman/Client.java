package com.amd.hangman;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {
    static int port;
    static String serverIP;
    private Socket socket;
    private Scanner scanner;
    private PrintWriter writer;

    public Client(String serverIP, int port) throws Exception {
        socket = new Socket(serverIP, port);
        scanner = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public static void main(String[] args) throws Exception {
        if (args.length <= 1) { // TODO: change args
            System.out.println("Incorrect arguments. Please provide a port number and server IP");
        } else {
            serverIP = args[0];
            port = Integer.parseInt(args[1]);
            Client client = new Client(serverIP, port);
           // client.play();
        }
    }

    public void sendGuess(String guess) {
        if (guess.length() > 1) {
            System.out.println(Message.ERROR_MULTIPLE_CHAR.getDescription());
        } else if (!Character.isAlphabetic(guess.charAt(0))) {
            System.out.println(Message.ERROR_NOT_LETTER.getDescription());
        }
        char[] guessPacket = Util.encodeGuessPacket(guess.charAt(0));
        // TODO: send packet to server
    }

    public void play() throws Exception {
        try {
            String response = scanner.nextLine();

            while(scanner.hasNextLine()) {
                response = scanner.nextLine();
                if (response.charAt(0) != 0) { // print message if it's a message packet
                    System.out.println(response.substring(2));
                } else { // otherwise print the different elements separately
                    List<String> decoded = Util.decodePacket(response);
                    for (int i = 0; i < decoded.get(3).length(); i++) {
                        System.out.print(decoded.get(3).charAt(i) + " ");
                    }
                    System.out.println();
                    System.out.print("Incorrect Guesses: ");
                    for (int i = 0; i < decoded.get(4).length(); i++) {
                        System.out.print(decoded.get(4).charAt(i) + " ");
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("closing socket");
            socket.close();
        }
    }
}
