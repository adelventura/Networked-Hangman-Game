package com.amd.hangman;

import java.io.EOFException;
import java.io.IOException;

public class TwoPlayerGame extends Thread {

    public GameConfig config;

    public TwoPlayerGame(GameConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        try {
            GameState state = setupGame();
            GameConfig.Player activePlayer = config.players.get(0);
            GameConfig.Player inactivePlayer = config.players.get(1);

            while (true) {
                inactivePlayer.output.write(MessagePacket.encode(MessagePacket.TWO_PLAYER_WAITING));
                activePlayer.output.write(ControlPacket.encode(state));
                handleGuess(activePlayer, state);

                if (state.isOver()) {
                    // end game
                    config.quitGame(true);
                    return;
                }

                // swap roles
                GameConfig.Player temp = activePlayer;
                activePlayer = inactivePlayer;
                inactivePlayer = temp;
            }
        } catch (EOFException eof) {
            config.quitGame(false);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGuess(GameConfig.Player player, GameState state) throws Exception {
        while (true) {
            player.output.write(MessagePacket.encode(MessagePacket.GUESS));
            String guess = MessagePacket.decode(player.input);
            String response = state.makeGuess(guess);
            if (response != null) {
                player.output.write(MessagePacket.encode(response));
            }

            if (state.isOver() || response == null) {
                return;
            }
        }
    }

    private GameState setupGame() {
        // No backdoor for two player, just pick a random word.
        String word = config.dictionary.getRandomWord();
        return new GameState(
                word,
                config.maxGuesses
        );
    }
}
