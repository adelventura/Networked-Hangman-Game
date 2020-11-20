package com.amd.hangman;

public class Util {
    // message packet from server
    public static char[] encodeMessagePacket(MessagePacket m) {
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

    public static char[] encodeLoseMessagePacket(MessagePacket m, String word) {
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
}
