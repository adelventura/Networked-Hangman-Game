package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
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
        byte[] receiveBuf;
        char[] sendBuf;
        scanner = new Scanner(System.in);

        // game setup
        receiveBuf = input.readUTF().toCharArray();
        String startMessage = decodeMessagePacket(receiveBuf);
        System.out.print(startMessage);
        String command = scanner.nextLine().toLowerCase();

        // startup commands -- y/n/#
        if (command.equals("n")) {
            sendBuf = encodeStartPacket(command);
            output.writeUTF(String.valueOf(sendBuf));
            socket.close();
            System.exit(0);
        } else if (command.equals("y")) {
            sendBuf = encodeStartPacket(command);
        } else {
            sendBuf = encodeStartPacket(command);
        }
        output.writeUTF(String.valueOf(sendBuf));

        // main game loop
        while (true) {
            try {
                receiveBuf = input.readUTF().toCharArray();
            } catch (EOFException eof) {
                // Server closed the connection, game complete.
                break;
            }

//            decodePacket
//                    length = readByte
//                    if (0) => decodeControl(input)
//                    else => decodeMessage(length, input)
            // decodeControl(input) throw IOEXception
            // length inpudt.readByte
            // length input.readByte
            //

            // check which packet type received
            if (receiveBuf[0] == 0) {  // message packet received
                ControlPacket packet = ControlPacket.decode(receiveBuf);
                System.out.println(packet.formatted());
                while (true) {
                    System.out.print("Letter to guess: ");
                    String guess = scanner.nextLine().toLowerCase(); // TODO: handle invalid input
                    if (guess.length() == 1) {
                        break;
                    }
                }

                sendBuf = encodeGuessPacket(guess);
                output.writeUTF(String.valueOf(sendBuf));
            } else {
                String message = MessagePacket.decode(receiveBuf);
                System.out.println(message + "\n");
            }
        }
    }

    // construct guess packet to send to server
    public static char[] encodeGuessPacket(Character guess) {
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


