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

    public Message makeGuess(char c) {
        if (guesses.contains(c)) { // already guessed this letter
            return Message.REPEAT;
        } else {
            guesses.add(c);
            if (word.indexOf(c) != -1) {
                if (hasGuessedWord()) { // correct guess and completes word
                    return Message.WIN;
                }
                return Message.CORRECT; // correct guess but doesn't complete word
            } else {
                if (incorrectGuessesRemaining != 0) { // incorrect guess
                    incorrectGuessesRemaining--;
                    return Message.INCORRECT;
                } else {
                    return Message.LOSE; // max incorrect guess reached
                }
            }
        }
    }

    public boolean hasGuessedWord() {
        for (int i = 0; i < word.length(); i++) {
            if (!guesses.contains(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String wordProgress() {
        StringBuilder progress = new StringBuilder();
        for (int i = 0; i < word.length(); i ++) {
            char c = word.charAt(i);
            if (guesses.contains(c)) {
                progress.append(" " + c + " ");
            } else {
                progress.append(" _ ");
            }
        }

        return progress.toString();
    }

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
