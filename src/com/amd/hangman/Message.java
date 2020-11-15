package com.amd.hangman;

public enum Message {
    REPEAT(0, "Repeated guess! Please try again"),
    CORRECT(1, "Correct guess!"),
    INCORRECT(2, "Incorrect guess."),
    WIN(3, "You win!"),
    LOSE(4, "You lose :("),
    GUESS(5, "Letter to guess: \n"),
    START(6, "Ready to start game? (y/n): "),
    ERROR_NOT_LETTER(7, "Error! Please guess one LETTER."),
    ERROR_MULTIPLE_CHAR(8, "Error! Please guess ONE letter.");

    private final int code;
    private final String description;

    private Message(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}
