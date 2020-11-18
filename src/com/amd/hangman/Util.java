package com.amd.hangman;

import java.util.List;

public class Util {
    // message packet from server
    public static char[] encodeMessagePacket(Message m) {
        char[] packet = new char[1024];
        int messageLength = m.getDescription().length();

        if (messageLength >= 10) {
            packet[0] = Integer.toString(messageLength).charAt(0);
            packet[1] = Integer.toString(messageLength).charAt(1);

            int i;
            for (i = 0; i < m.getDescription().length(); i++) {
                packet[i + 2] = m.getDescription().charAt(i);
            }
        } else {
            packet = new char[1024];
            packet[0] = Integer.toString(messageLength).charAt(0);

            int i;
            for (i = 0; i < m.getDescription().length(); i++) {
                packet[i + 1] = m.getDescription().charAt(i);
            }
        }

        return packet;
    }

    public static char[] encodeLoseMessagePacket(Message m, String word) {
        char[] packet = new char[1024];
        int messageLength = m.getDescription().length();
        messageLength += word.length();

        if (messageLength >= 10) {
            packet[0] = Integer.toString(messageLength).charAt(0);
            packet[1] = Integer.toString(messageLength).charAt(1);

            int i;
            for (i = 0; i < m.getDescription().length(); i++) {
                packet[i + 2] = m.getDescription().charAt(i);
            }

            i += 2;
            for (int j = 0; j < word.length(); j++) {
                packet[i + j] = word.charAt(j);
            }

        } else {
            packet = new char[1024];
            packet[0] = Integer.toString(messageLength).charAt(0);

            int i;
            for (i = 0; i < m.getDescription().length(); i++) {
                packet[i + 1] = m.getDescription().charAt(i);
            }

            i += 1;
            for (int j = 0; j < word.length(); j++) {
                packet[i + j] = word.charAt(j);
            }
        }

        return packet;
    }

    // game control packet from server
    public static char[] encodeControlPacket(GameState gameState) {
        int wordLength = gameState.getWord().length();
        int incorrectGuesses = gameState.incorrectGuesses().size();
        List<Character> incorrectGuessList = gameState.incorrectGuesses();
        String progress = gameState.wordProgress();

        char[] packet = new char[1024];

        packet[0] = '0'; // flag for message packet is 0
        packet[1] = Integer.toString(wordLength).charAt(0);
        packet[2] = Integer.toString(incorrectGuesses).charAt(0);

        for (int i = 0; i < progress.length(); i++) {
            packet[i + 3] = progress.charAt(i);
        }
        for (int i = 0; i < incorrectGuessList.size(); i++) {
            packet[i + 3 + progress.length()] = incorrectGuessList.get(i);
        }

        return packet;
    }
}
