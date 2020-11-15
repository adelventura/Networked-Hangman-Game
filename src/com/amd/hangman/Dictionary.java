package com.amd.hangman;

import java.io.BufferedReader;
import java.io.FileReader;

public class Dictionary {

    public static Dictionary defaultDictionary() {
        return new Dictionary(new String[] {
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
        });
    }

    public static Dictionary fromFile(String path) throws Exception {
        String[] words;
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        int i = 0;
        while (line.charAt(i) != ' ') {
            i++;
        }

        int dictionarySize = Integer.parseInt(line.substring(i + 1));
        words = new String[dictionarySize];

        line = reader.readLine();
        i = 0;
        while (line != null) {
            words[i] = line;
            i++;
            line = reader.readLine();
        }

        return new Dictionary(words);
    }

    private String[] words;

    private Dictionary(String[] words) {
        this.words = words;
    }

    public String getRandomWord() {
        int wordIndex = (int)(Math.random() * words.length);
        return words[wordIndex];
    }

    public String getWord(int index) {
        return words[index];
    }
}
