package ProgFunkcyjne.InterfaceFunkcuyjny.Projekt;

import java.util.Random;
import java.util.function.UnaryOperator;

public class Transformer {

    public UnaryOperator<String> provideCrazyWords() {

        return (text) -> {
            Random random = new Random();
            int size = text.length();
            StringBuilder crazyWord = new StringBuilder();

            char[] characters = text.toCharArray();
            int control = 0;

            while (control != size) {
                int randomIndex = random.nextInt(size);

                if (characters[randomIndex] != 0) {
                    String character = String.valueOf(characters[randomIndex]);
                    characters[randomIndex] = 0;
                    crazyWord.append(character);
                    control++;
                }
            }

            return crazyWord.toString();
        };
    }
}