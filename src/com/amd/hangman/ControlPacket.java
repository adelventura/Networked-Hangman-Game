package com.amd.hangman;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// [0][word length][incorrect guesses count][word progress][incorrect guesses]
public class ControlPacket {
    public final static byte HEADER = 0;

    public static byte[] encode(GameState gameState) {
        ControlPacket controlPacket = new ControlPacket(gameState.wordProgress(), gameState.incorrectGuesses());
        return controlPacket.encode();
    }

    public static ControlPacket decode(DataInputStream input) throws IOException {
        int wordLength = input.readByte();
        int incorrectGuessesLength = input.readByte();

        StringBuilder wordProgress = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            wordProgress.append((char) input.readByte());
        }

        ArrayList<Character> incorrectGuesses = new ArrayList<>();
        for (int i = 0; i < incorrectGuessesLength; i++) {
            char c = (char) input.readByte();
            incorrectGuesses.add(c);
        }

        return new ControlPacket(
                wordProgress.toString(),
                incorrectGuesses
        );
    }

    public final String wordProgress;
    public final List<Character> incorrectGuesses;

    public ControlPacket(String wordProgress, List<Character> incorrectGuesses) {
        this.wordProgress = wordProgress;
        this.incorrectGuesses = incorrectGuesses;
    }

    public byte[] encode() {
        int packetSize = 1 + 1 + 1 + wordProgress.length() + incorrectGuesses.size();
        byte[] packet = new byte[packetSize];

        packet[0] = HEADER;
        packet[1] = (byte) wordProgress.length();
        packet[2] = (byte) incorrectGuesses.size();

        for (int i = 0; i < wordProgress.length(); i++) {
            char c = wordProgress.charAt(i);
            packet[i + 3] = (byte) c;
        }
        for (int i = 0; i < incorrectGuesses.size(); i++) {
            char c = incorrectGuesses.get(i);
            packet[i + 3 + wordProgress.length()] = (byte) c;
        }

        return packet;
    }

    public String formatted() {
        StringBuilder builder = new StringBuilder();
        for (Character c : wordProgress.toCharArray()) {
            builder.append(c + " ");
        }
        builder.append('\n');
        builder.append("Incorrect Guesses: ");
        for (Character c : incorrectGuesses) {
            builder.append(c + " ");
        }
        builder.append('\n');
        return builder.toString();
    }
}
