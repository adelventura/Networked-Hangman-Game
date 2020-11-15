package com.amd.hangman;

import java.util.*;

public class Util {
    // message packet from server
    public static char[] encodeMessagePacket(Message m) {
        char[] packet;
        int messageLength = m.getDescription().length();
        if (messageLength >= 10) {
            packet = new char[3 + messageLength];
            packet[0] = Integer.toString(messageLength).charAt(0);
            packet[1] = Integer.toString(messageLength).charAt(1);
            packet[2] = '|';
            for (int i = 3; i < packet.length; i++) {
                packet[i] = m.getDescription().charAt(i - 3);
            }
        } else {
            packet = new char[2 + messageLength];
            packet[0] = Integer.toString(messageLength).charAt(0);
            packet[1] = '|';

            for (int i = 2; i < packet.length; i++) {
                packet[i] = m.getDescription().charAt(i - 2);
            }
        }

        return packet;
    }

    // game control packet from server
    public static char[] encodeControlPacket(Game game) {
        int wordLength = game.getWord().length();
        int incorrectGuesses = game.incorrectGuesses().size();
        List<Character> incorrectGuessList = game.incorrectGuesses();
        String progress = game.wordProgress();

        char[] packet = new char[8 + progress.length() + incorrectGuessList.size()];

        packet[0] = '0'; // message flag 0
        packet[1] = '|';
        packet[2] = Integer.toString(wordLength).charAt(0);
        packet[3] = '|';
        packet[4] = Integer.toString(incorrectGuesses).charAt(0);
        packet[5] = '|';

        for (int i = 0; i < progress.length(); i++) {
            packet[i + 6] = progress.charAt(i);
        }
        packet[6 + progress.length()] = '|';
        for (int i = 0; i < incorrectGuessList.size(); i++) {
            packet[i + 7 + progress.length()] = incorrectGuessList.get(i);
        }
        packet[7 + progress.length() + incorrectGuessList.size()] = '|';

        return packet;
    }

    // guess packet from client
    public static char[] encodeGuessPacket(char guess) {
        char c = Character.toLowerCase(guess);
        return new char[]{1, '|', c};
    }

    public static List<String> decodePacket(String packet) {
        char[] c = packet.toCharArray();
        List<String> decoded = new ArrayList<>();
        decoded.add(c[2] + "");
        decoded.add(c[4] + "");

        int i = 6;
        String progress = "";
        while (c[i] != '|') {
            progress += c[i];
            i++;
        }
        decoded.add(progress);
        i++;
        String incorrectGuesses = "";
        while (c[i] != '|') {
            incorrectGuesses += c[i];
            i++;
        }
        decoded.add(incorrectGuesses);
        return decoded;
    }
}
