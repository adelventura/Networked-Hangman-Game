package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SinglePlayerGame extends Thread {

    public GameConfig config;
    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output;

    public SinglePlayerGame(GameConfig config) {
        this.config = config;
        GameConfig.Player player = config.players.get(0);
        this.socket = player.socket;
        this.input = player.input;
        this.output = player.output;
    }

    @Override
    public void run() {
        Server.games.add(config);

        try {
            GameState state = setupGame();
            if (state == null) {
                config.quitGame(true);
                return;
            }

            while (true) {
                output.write(ControlPacket.encode(state));
                handleGuess(state);

                if (state.isOver()) {
                    // end game
                    config.quitGame(true);
                    return;
                }
            }
        } catch (Exception e) {
            config.quitGame(false);
        }
    }

    private void handleGuess(GameState state) throws Exception {
        while (true) {
            output.write(MessagePacket.encode(MessagePacket.GUESS));
            String guess = MessagePacket.decode(input);
            String response = state.makeGuess(guess);
            if (response != null) {
                if (state.isOver()) {
                    // if the game is over also print the control packet.
                    output.write(ControlPacket.encode(state));
                }

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
                    // dictionary backdoor is 1 indexed.
                    int backdoorWordIndex = Integer.parseInt(response) + 1;
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
}
