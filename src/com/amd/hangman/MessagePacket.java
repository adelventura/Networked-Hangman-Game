package com.amd.hangman;

import java.io.DataInputStream;
import java.io.IOException;

public class MessagePacket {
    public final static String REPEATED_GUESS = "Error! Letter _ has been guessed before. Please guess another letter.\n";
    public final static String WIN = "You win!\n";
    public final static String LOSE = "You lose: ";
    public final static String GUESS = "Letter to guess: ";
    public final static String START = "Ready to start game? (y/n): ";
    public final static String ERROR_NOT_LETTER = "Error! Please guess one LETTER.\n";
    public final static String ERROR_MULTIPLE_CHAR = "Error! Please guess ONE letter.\n";
    public final static String ERROR_TOO_MANY_CLIENTS = "Server already connected to three clients. Please try again later.\n";

    public final static String TWO_PLAYER_PROMPT = "Two Player? (y/n): ";
    public final static String TWO_PLAYER_WAITING = "Waiting for other player!\n";
    public final static String TWO_PLAYER_GAME_START = "Game Starting!\n";
    public final static String TWO_PLAYER_TURN_START = "Your Turn!\n";

    public static byte[] encode(String message) {
        int packetLength = 1 + message.length();
        byte[] packet = new byte[packetLength];

        packet[0] = (byte) message.length();
        for (int i = 0; i < message.length(); i++) {
            packet[1 + i] = (byte) message.charAt(i);
        }

        return packet;
    }

    public static String decode(DataInputStream input) throws IOException {
        int length = input.readByte();
        return decode(length, input);
    }

    public static String decode(int length, DataInputStream input) throws IOException {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < length; i++) {
            message.append((char) input.readByte());
        }
        return message.toString();
    }
}
