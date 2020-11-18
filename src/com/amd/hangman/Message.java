package com.amd.hangman;

public enum Message {
    REPEAT(0, "Repeated guess! Please try again"),
    WIN(1, "You win!"),
    LOSE(2, "You lose: "),
    GUESS(3, "Letter to guess: \n"),
    START(4, "Ready to start game? (y/n): "),
    ERROR_NOT_LETTER(5, "Error! Please guess one LETTER."),
    ERROR_MULTIPLE_CHAR(6, "Error! Please guess ONE letter."),
    ERROR_TOO_MANY_CLIENTS(7, "Server already connected to three clients. Please try again later.");

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
