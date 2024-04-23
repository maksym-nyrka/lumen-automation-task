package utils;

import java.util.Random;

public class Utils {

    public static int generateRandomNumber(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static int generateRandomNumber() {
        return generateRandomNumber(1, 2000000000);
    }

    public static void main(String[] args) {
        int randomNumber = generateRandomNumber(1, 100);
        System.out.println("Random Number: " + randomNumber);
    }
}
