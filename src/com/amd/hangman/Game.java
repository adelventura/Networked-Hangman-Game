package com.amd.hangman;

import jdk.jshell.execution.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Game extends Thread {

    public GameConfig config;
    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output;

    public Game(GameConfig config, Socket socket, DataInputStream input, DataOutputStream output) {
        this.config = config;
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            GameState state = setupGame();
            if (state == null) {
                connectionClosed(true);
                return;
            }

            while (true) {
                output.write(ControlPacket.encode(state));
                handleGuess(state);

                if (state.isOver()) {
                    // end game
                    connectionClosed(true);
                    return;
                }
            }
        } catch (EOFException eof) {
            connectionClosed(false);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGuess(GameState state) throws Exception {
        while (true) {
            output.write(MessagePacket.encode(MessagePacket.GUESS));
            String guess = MessagePacket.decode(input);
            String response = state.makeGuess(guess);
            if (response != null) {
                output.write(MessagePacket.encode(response));
            }

            if (state.isOver() || response == null) {
                return;
            }
        }
    }

    private GameState setupGame() throws IOException {
        String word = null;
        do {
            output.write(MessagePacket.encode(MessagePacket.START));
            String response = MessagePacket.decode(input).toLowerCase();
            if (response.equals("n")) {
                return null;
            }

            if (response.equals("y")) {
                word = config.dictionary.getRandomWord();
            } else {
                try {
                    int backdoorWordIndex = Integer.parseInt(response);
                    word = config.dictionary.getWord(backdoorWordIndex);
                } catch (NumberFormatException ignored) {
                }
            }
        } while (word == null);

        return new GameState(
                word,
                config.maxGuesses
        );
    }

    private void connectionClosed(Boolean closeSocket) {
        System.out.println("Disconnected connection: " + socket.toString());
        Server.socketCount -= 1;

        if (closeSocket) {
            try {
                output.flush();
                socket.close();
            } catch (Exception e) {
                // ignore. already closing the socket.
            }
        }
    }
}
