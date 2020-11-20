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

                MessagePacket messagePacket = state.makeGuess(rcv);
                if (messagePacket == null) {
                    // no message to report
                } else if (messagePacket == MessagePacket.LOSE) {
                    send = Util.encodeLoseMessagePacket(messagePacket, state.getWord());
                    closeConnection(send);
                    break;
                    // need to "gracefully exit" ?
                } else if (messagePacket == MessagePacket.WIN) {
                    send = Util.encodeMessagePacket(messagePacket);
                    closeConnection(send);
                    break;
                    // need to "gracefully exit" ?
                } else {
                    send = Util.encodeMessagePacket(messagePacket);
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

        send = Util.encodeMessagePacket(MessagePacket.START);
        output.writeUTF(String.valueOf(send));
        rcv = input.readUTF().toCharArray();

        char command = rcv[1];

        String word;
        if (command == 'n') {
            output.flush();
            Server.socketCount -= 1;
            socket.close();
            return null;
        } else if (command == 'y') {
            word = config.dictionary.getRandomWord();
        } else {
            word = config.dictionary.getWord(Integer.parseInt(command + ""));
        }

        return new GameState(
                word,
                config.maxGuesses
        );
    }

    private void closeConnection(char[] buffer) throws Exception {
        Server.socketCount -= 1;
        output.writeUTF(String.valueOf(buffer));
        output.flush();
        socket.close();
    }
}
