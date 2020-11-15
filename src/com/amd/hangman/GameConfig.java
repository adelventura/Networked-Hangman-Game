package com.amd.hangman;

public class GameConfig {
    public Dictionary dictionary;
    public int maxGuesses;

    public GameConfig(Dictionary dictionary, int maxGuesses) {
        this.dictionary = dictionary;
        this.maxGuesses = maxGuesses;
    }
}
