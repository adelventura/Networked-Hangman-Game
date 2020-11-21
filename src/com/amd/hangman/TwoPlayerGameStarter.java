package com.amd.hangman;

import java.io.EOFException;

public class TwoPlayerGameStarter extends Thread {

    private GameConfig config;
    private GameConfig.Player player;

    public TwoPlayerGameStarter(GameConfig config) {
        this.config = config;
        this.player = config.players.get(0);
    }

    @Override
    public void run() {
        try {
            player.output.write(MessagePacket.encode(MessagePacket.TWO_PLAYER_PROMPT));
            String response = MessagePacket.decode(player.input).toLowerCase();
            if (!response.equals("y")) {
                // Just start a regular game.
                Thread gameThread = new SinglePlayerGame(config);
                gameThread.start();
            } else {
                // Try to find a two player game waiting for a second player.
                for (GameConfig otherConfig : Server.games) {
                    if (otherConfig.waitingForSecondPlayer()) {
                        // Found a pending game, add current player to the pending game.
                        otherConfig.addPlayer(player);
                        // start the game.
                        TwoPlayerGame game = new TwoPlayerGame(otherConfig);
                        game.start();

                        return;
                    }
                }

                // No pending game found, add to game list to wait for second player.
                player.output.write(MessagePacket.encode(MessagePacket.TWO_PLAYER_WAITING));
                Server.games.add(config);
            }
        } catch (Exception e) {
            config.quitGame(false);
        }
    }
}

