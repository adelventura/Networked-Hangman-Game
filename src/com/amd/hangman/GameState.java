package com.amd.hangman;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private final String word;
    private Set<Character> guesses;
    private volatile int incorrectGuessesRemaining;

    public GameState(String word, int maxGuesses) {
        this.word = word;
        this.incorrectGuessesRemaining = maxGuesses;
        guesses = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public String makeGuess(String guess) {
        guess = guess.toLowerCase();
        if (guess.length() != 1) {
            return MessagePacket.ERROR_MULTIPLE_CHAR;
        }

        char c = guess.charAt(0);
        if (!Character.isAlphabetic(c)) {
            return MessagePacket.ERROR_NOT_LETTER;
        }

        if (guesses.contains(c)) { // already guessed this letter
            String repeatedGuessMessage = MessagePacket.REPEATED_GUESS.replace('_', c);
            return repeatedGuessMessage;
        } else {
            guesses.add(c);
            if (word.indexOf(c) != -1) { // correct guess
                if (hasGuessedWord()) { // completes word
                    return MessagePacket.WIN;
                }
                return null; // doesn't complete word
            } else {
                // incorrect guess
                incorrectGuessesRemaining--;
                if (incorrectGuessesRemaining == 0) {
                    return MessagePacket.LOSE + word + "\n"; // max incorrect guess reached
                }

                return null;
            }
        }
    }

    // determine whether word has been guessed
    public boolean hasGuessedWord() {
        for (int i = 0; i < word.length(); i++) {
            // if letter found in word that hasn't been guessed, return false
            if (!guesses.contains(word.charAt(i))) {
                return false;
            }
        }
        // else get every letter in word has been guessed, return true
        return true;
    }

    public boolean isOver() {
        return incorrectGuessesRemaining <= 0 || hasGuessedWord();
    }

    public String wordProgress() {
        StringBuilder progress = new StringBuilder();
        for (int i = 0; i < word.length(); i ++) {
            char c = word.charAt(i);
            if (guesses.contains(c)) {
                progress.append(c);
            } else {
                progress.append("_");
            }
        }

        return progress.toString();
    }

    // get list of incorrect guesses
    public List<Character> incorrectGuesses() {
        List<Character> incorrectGuesses = new ArrayList<>();
        for (Character c : guesses) {
            if (word.indexOf(c) == -1) {
                incorrectGuesses.add(c);
            }
        }

        return incorrectGuesses;
    }

    public String getWord() {
        return word;
    }
}
