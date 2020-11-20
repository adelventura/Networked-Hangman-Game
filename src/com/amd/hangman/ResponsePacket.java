package com.amd.hangman;

public class ResponsePacket {

    public static byte[] encode(char response) {
        byte[] packet = new byte[2];

        packet[0] = 1;
        packet[1] = (byte) response;

        return packet;
    }

    public static char decode(byte[] packet) {
        char c = (char) packet[1];

        if (Character.isDigit(c)) {
            return c;
        }

        return Character.toLowerCase(c);
    }
}
