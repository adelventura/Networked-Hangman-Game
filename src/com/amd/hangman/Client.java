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
            client.play();
        }
    }

    public void play() throws IOException {
        byte[] receiveBuf;
        byte[] sendBuf;
        scanner = new Scanner(System.in);

        // game setup
        receiveBuf = input.readUTF().toCharArray();
        String startMessage = MessagePacket.decode(receiveBuf);

        // startup commands -- y/n/#
        char command = getCommandFromUser(startMessage);
        if (command == 'n') {
            sendBuf = ResponsePacket.encode(command);
            output.writeUTF(String.valueOf(sendBuf));
            socket.close();
            System.exit(0);
        } else {
            sendBuf = ResponsePacket.encode(command);
        }
        output.writeUTF(String.valueOf(sendBuf));

        // main game loop
        while (true) {
            try {
                receiveBuf = input.readUTF();
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

                sendBuf = ResponsePacket.encode(guess);
                output.writeUTF(String.valueOf(sendBuf));
            } else {
                String message = MessagePacket.decode(receiveBuf);
                System.out.println(message + "\n");
            }
        }
    }

    private char getCommandFromUser(String message) {
        char command;

        while (true) {
            System.out.print(message);
            String s = scanner.nextLine().toLowerCase();
            if (s.length() == 1) {
                command = s.charAt(0);
                if (!Character.isAlphabetic(command) || !Character.isDigit(command)) {
                    System.out.println("Please enter one LETTER.");
                } else {
                    break;
                }
            } else {
                System.out.println("Please enter ONE letter.");
            }
        }
        return command;
    }
}


