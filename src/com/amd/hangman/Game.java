package com.amd.hangman;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

public class Game extends Thread {

    public GameConfig config;
    public Socket socket;
    public BufferedReader input;
    public DataOutputStream output; // TODO: BufferedWrite??

    public Game(GameConfig config, Socket socket, BufferedReader input, DataOutputStream output) {
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
                return;
            }

            while (true) {

                output.writeUTF(
                    state.wordProgress() + "\n"
                );
                output.writeUTF("Incorrect Guesses: " + state.incorrectGuesses() + "\n");
                output.writeUTF("Letter to guess: ");

                String command = input.readLine();
                state.makeGuess(command.toLowerCase().charAt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GameState setupGame() throws Exception {
        output.writeUTF(
                "Ready to start game? (y/n) "
        );
        String command = input.readLine();
        if (command.equals("n")) {
            socket.close();
            return null;
        }

        String word;
        if (command.equals("y")) {
            word = config.dictionary.getRandomWord();
        } else {
            word = config.dictionary.getWord(Integer.parseInt(command));
        }

        return new GameState(
                word,
                config.maxGuesses
        );
    }

//    public static char[] receiveGuess(char[] guessPacket) {
//        char guess = guessPacket[1];
//        Message result = gameState.makeGuess(guess);
//        if (result.getCode() == 4 || result.getCode() == 5) { // game over (either won or lost)
//            return Util.encodeMessagePacket(result);
//        } else {
//            // and start next round?
//            return Util.encodeMessagePacket(result);
//        }
//    }
}
