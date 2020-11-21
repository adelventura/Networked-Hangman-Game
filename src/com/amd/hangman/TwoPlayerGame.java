package com.amd.hangman;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

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
                byte[] gameEndingResponse = handleGuess(activePlayer, state);
                if (gameEndingResponse != null) {
                    // game ended. print current control and you win / lose.
                    inactivePlayer.output.write(ControlPacket.encode(state));
                    activePlayer.output.write(ControlPacket.encode(state));
                    inactivePlayer.output.write(gameEndingResponse);
                    activePlayer.output.write(gameEndingResponse);
                    config.quitGame(true);
                    return;
                }

                // swap roles
                GameConfig.Player temp = activePlayer;
                activePlayer = inactivePlayer;
                inactivePlayer = temp;
            }
        } catch (Exception e) {
            config.quitGame(false);
        }
    }

    private byte[] handleGuess(GameConfig.Player player, GameState state) throws Exception {
        while (true) {
            player.output.write(MessagePacket.encode(MessagePacket.GUESS));
            String guess = MessagePacket.decode(player.input);
            int previousIncorrectGuesses = state.incorrectGuesses().size();
            String response = state.makeGuess(guess);

            if (response == null) {
                // valid guess. inform active user.
                if (previousIncorrectGuesses == state.incorrectGuesses().size()) {
                    // correct guess
                    player.output.write(MessagePacket.encode(MessagePacket.CORRECT_GUESS));
                } else {
                    // incorrect guess
                    player.output.write(MessagePacket.encode(MessagePacket.INCORRECT_GUESS));
                }

                return null;
            } else {
                byte[] responsePacket = MessagePacket.encode(response);
                if (state.isOver()) {
                    // return a game ending response
                    return responsePacket;
                }

                // send all other responses to the player directly.
                player.output.write(MessagePacket.encode(response));
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
