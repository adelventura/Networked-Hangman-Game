package com.amd.hangman;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameState {
    private String word;
    private Set<Character> guesses;
    private int incorrectGuessesRemaining;

    public GameState(String word, int maxGuesses) {
        this.word = word;
        this.incorrectGuessesRemaining = maxGuesses;
        guesses = new HashSet<>();
    }

    public Message makeGuess(char[] packet) {
        if (!Character.isAlphabetic(packet[1])) { // guess isn't a letter
            return Message.ERROR_NOT_LETTER;
        } else if (packet[2] != '\u0000') { // guess is multiple characters
            return Message.ERROR_MULTIPLE_CHAR;
        }

        char c = packet[1];
        if (guesses.contains(c)) { // already guessed this letter
            return Message.REPEAT;
        } else {
            guesses.add(c);
            if (word.indexOf(c) != -1) { // correct guess
                if (hasGuessedWord()) { // completes word
                    return Message.WIN;
                }
                return null; // doesn't complete word
            } else {
                if (incorrectGuessesRemaining != 0) { // incorrect guess
                    incorrectGuessesRemaining--;
                    return null;
                } else {
                    return Message.LOSE; // max incorrect guess reached
                }
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
