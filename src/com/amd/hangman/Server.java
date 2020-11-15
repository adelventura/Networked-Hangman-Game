package com.amd.hangman;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private static String[] defaultDictionary = {
            "horse",
            "house",
            "apple",
            "blanket",
            "jacket",
            "cottage",
            "lunch",
            "bread",
            "homework",
            "soap",
            "sauce",
            "pillow",
            "trash",
            "puppy",
            "sand"
    };

    static int port;
    static String[] dictionary;
    static Game game;

    // port
    // [optional] file
    public static void main(String[] args) {
        // 1. read args
        // 2. setup wordbank
        // 3. setup games
        // 4. start server loop
        if (args.length < 1) { // TODO: change args
            // incorrect num args
            System.out.println("Incorrect arguments. Please provide a port number");
        } else {
            port = Integer.parseInt(args[0]);

            if (args.length == 2) {
                // use file for dictionary
                dictionary = getDictionary(args[1]);
            } else {
                // use default dictionary
                dictionary = defaultDictionary;
            }
        }

        String randomWord = dictionary[(int)(Math.random() * dictionary.length)];
        Game game = new Game(randomWord, 6);
    }

    public static String[] getDictionary(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            int i = 0;
            while (line.charAt(i) != ' ') {
                i++;
            }

            int dictionarySize = Integer.parseInt(line.substring(i + 1));
            dictionary = new String[dictionarySize];

            line = reader.readLine();
            i = 0;
            while(line != null) {
                dictionary[i] = line;
                i++;
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound Exception");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }

        return dictionary;
    }

    public static void setupGame(int numPlayers) {
        if (numPlayers == 2) {
            // handle two-player mode?
        } else {
           // one-payer mode
        }
    }

    public static char[] receiveGuess(char[] guessPacket) {
        char guess = guessPacket[1];
        Message result = game.makeGuess(guess);
        if (result.getCode() == 4 || result.getCode() == 5) { // game over (either won or lost)
            return Util.encodeMessagePacket(result);
        } else {
            // and start next round?
            return Util.encodeMessagePacket(result);
        }
    }
}
