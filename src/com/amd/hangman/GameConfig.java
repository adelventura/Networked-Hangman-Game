package com.amd.hangman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class GameConfig {
    public static class Player {
        public final Socket socket;
        public final DataInputStream input;
        public final DataOutputStream output;

        public Player(Socket socket, DataInputStream input, DataOutputStream output) {
            this.socket = socket;
            this.input = input;
            this.output = output;
        }
    }

    public final Dictionary dictionary;
    public final int maxGuesses;
    public final List<Player> players;
    public volatile boolean isTwoPlayerGame;

    public GameConfig(Dictionary dictionary, int maxGuesses, Player firstPlayer) {
        this.dictionary = dictionary;
        this.maxGuesses = maxGuesses;
        this.players = new Vector<>();

        addPlayer(firstPlayer);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public boolean waitingForSecondPlayer() {
        return (players.size() == 1 && isTwoPlayerGame);
    }

    public void quitGame(Boolean closeSocket) {
        Server.games.remove(this);

        if (closeSocket) {
            try {
                for (GameConfig.Player player : players) {
                    System.out.println("Disconnected connection: " + player.socket.toString());
                    player.output.flush();
                    player.socket.close();
                }
            } catch (Exception e) {
                // ignore. already closing the socket.
            }
        }
    }
}
