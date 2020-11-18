package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
        if (args.length <= 1) { // TODO: change args
            System.out.println("Incorrect arguments. Please provide a port number and server IP");
        } else {
            serverIP = args[0];
            port = Integer.parseInt(args[1]);
            Client client = new Client(serverIP, port);
            client.run();
        }
    }

    public void run() throws IOException {
        char[] receiveBuf;
        char[] sendBuf;
        scanner = new Scanner(System.in);

        // game setup
        receiveBuf = input.readUTF().toCharArray();
        String startMessage = decodeMessagePacket(receiveBuf);
        System.out.print(startMessage);
        String command = scanner.nextLine().toLowerCase();

        if (command.equals("n")) {
            sendBuf = encodeStartPacket(command);
        } else if (command.equals("y")) {
            sendBuf = encodeStartPacket(command);
        } else {
            sendBuf = encodeStartPacket(command);
        }
        output.writeUTF(String.valueOf(sendBuf));

        // main game loop
        while (true) {
            receiveBuf = input.readUTF().toCharArray();
            if (receiveBuf[0] != '0') {  // message packet received
                String message = decodeMessagePacket(receiveBuf);
                System.out.println(message + "\n");

                if (message.equals(Message.WIN.getDescription())) {
                    System.exit(0); // where to close connections?
                } else {
                    String m = String.valueOf(receiveBuf);
                    String substring = m.substring(2, 12);
                    if (substring.equals(Message.LOSE.getDescription())) {
                        System.exit(0);
                    }
                }
            } else {  // control packet received
                List<String> packetContents = decodeControlPacket(receiveBuf);

                System.out.println(packetContents.get(0));
                System.out.println("Incorrect Guesses: " + packetContents.get(1) + "\n");

                System.out.print("Letter to guess: ");
                String guess = scanner.nextLine().toLowerCase(); // TODO: handle invalid input
                sendBuf = encodeGuessPacket(guess);
                output.writeUTF(String.valueOf(sendBuf));
            }
        }
    }

    // pull message from message packet
    public String decodeMessagePacket(char[] buffer) {
        String message = "";
        int length = ((int) buffer[0]) - 48;

        if (Character.isDigit(buffer[1])) {
            length *= 10;
            int secondDigit = ((int) buffer[1]) - 48;
            int messageStartIndex = 2;
            length += secondDigit;
            for (int j = messageStartIndex; j < (messageStartIndex + length); j++) {
                message += buffer[j];
            }
        } else {
            int messageStartIndex = 1;
            for (int j = messageStartIndex; j < (messageStartIndex + length); j++) {
                message += buffer[j];
            }
        }

        return message;
    }

    // pull word progress and incorrect guesses from control packet
    public List<String> decodeControlPacket(char[] buffer) {
        List<String> packetContents = new ArrayList<>();
        String progress = "";
        String incorrectGuesses = "";

        int wordLength = ((int) buffer[1]) - 48;
        int guessesLength = ((int) buffer[2]) - 48;

        int progressStartIndex = 3;
        int progressEndIndex = progressStartIndex + wordLength;
        for (int j = progressStartIndex; j < progressEndIndex; j++) {
            progress += buffer[j];
            progress += " ";
        }
        packetContents.add(progress);

        int guessesStartIndex = 3 + wordLength;
        int guessesEndIndex = guessesStartIndex + guessesLength;
        for (int j = guessesStartIndex; j < guessesEndIndex; j++) {
            incorrectGuesses += buffer[j];
            incorrectGuesses += " ";
        }
        packetContents.add(incorrectGuesses);

        return packetContents;
    }

    // construct guess packet to send to server
    public static char[] encodeGuessPacket(String guess) {
        char[] packet = new char[1024];
        packet[0] = '1';

        for (int i = 0; i < guess.length(); i++) {
            packet[i + 1] = guess.charAt(i);
        }

        return packet;
    }

    // construct start packet to send to server (y, n, or number)
    public static char[] encodeStartPacket(String command) {
        char[] packet = new char[1024];
        packet[0] = '1';

        for (int i = 0; i < command.length(); i++) {
            packet[i + 1] = command.charAt(i);
        }

        return packet;
    }
}


