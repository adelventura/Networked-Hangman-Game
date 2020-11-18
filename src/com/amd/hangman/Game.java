package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Game extends Thread {

    public GameConfig config;
    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output; // TODO: BufferedWrite??

    public Game(GameConfig config, Socket socket, DataInputStream input, DataOutputStream output) {
        this.config = config;
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        char[] send;
        char[] rcv;
        try {
            GameState state = setupGame();
            if (state == null) {
                return;
            }

            while (true) {
                send = Util.encodeControlPacket(state);
                output.writeUTF(String.valueOf(send));
                rcv = input.readUTF().toCharArray();

                Message message = state.makeGuess(rcv);
                if (message == null) {
                    // no message to report
                } else if (message == Message.LOSE) {
                    send = Util.encodeLoseMessagePacket(message, state.getWord());
                    output.writeUTF(String.valueOf(send));
                    // need to "gracefully exit" ?
                } else if (message == Message.WIN) {
                    send = Util.encodeMessagePacket(message);
                    output.writeUTF(String.valueOf(send));
                    // need to "gracefully exit" ?
                } else {
                    send = Util.encodeMessagePacket(message);
                    output.writeUTF(String.valueOf(send));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GameState setupGame() throws Exception {
        char[] send;
        char[] rcv;

        send = Util.encodeMessagePacket(Message.START);
        output.writeUTF(String.valueOf(send));
        rcv = input.readUTF().toCharArray();

        char command = rcv[1];

        if (command == 'n') {
            System.out.println("Server Printing: n received! closing socket!");
            socket.close();
            return null;
        }

        String word;
        if (command == 'y') {
            word = config.dictionary.getRandomWord();
        } else {
            word = config.dictionary.getWord(Integer.parseInt(command + ""));
        }

        return new GameState(
                word,
                config.maxGuesses
        );
    }
}
