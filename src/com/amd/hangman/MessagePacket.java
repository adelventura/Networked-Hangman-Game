package com.amd.hangman;

public class MessagePacket {
    public final static String REPEATED_GUESS = "Repeated guess! Please try again";
    public final static String WIN = "You win!";
    public final static String LOSE = "You lose: ";
    public final static String GUESS = "Letter to guess: \n";
    public final static String START = "Ready to start game? (y/n): ";
    public final static String ERROR_NOT_LETTER = "Error! Please guess one LETTER.";
    public final static String ERROR_MULTIPLE_CHAR = "Error! Please guess ONE letter.";
    public final static String ERROR_TOO_MANY_CLIENTS = "Server already connected to three clients. Please try again later.";

    public static byte[] encode(String message) {
        int packetLength = 1 + message.length();
        byte[] packet = new byte[packetLength];

        packet[0] = (byte) message.length();
        for (int i = 0; i < message.length(); i++) {
            packet[1 + i] = (byte) message.charAt(i);
        }

        return packet;
    }

    public static String decode(byte[] packet) {
        int length = packet[0];
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < length; i++) {
            message.append(packet[1 + i]);
        }
        return message.toString();
    }
}
