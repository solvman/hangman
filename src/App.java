import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class App {
    private static final int MAX_GUESSES = 6;
    private static final String DICTIONARY_FILE_PATH = "data/dictionary.txt";

    public static void main(String[] args) {
        startGame();
    }

    public static void startGame() {
        List<String> dictionary = loadDictionary();

        try (Scanner inputScanner = new Scanner(System.in)) {
            while (true) {
                if (!shouldStartNewRound(inputScanner)) {
                    return;
                }
                String secretWord = generateSecretWord(dictionary);
                startNewRound(secretWord, inputScanner);
            }
        } catch (IllegalStateException exception) {
            System.out.println("Error: No words found in dictionary file.");
            System.exit(1);
        }
    }

    private static List<String> loadDictionary() {
        List<String> dictionary = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(DICTIONARY_FILE_PATH))) {
            while (fileScanner.hasNextLine()) {
                String word = fileScanner.nextLine();
                dictionary.add(word.toLowerCase());
            }
        } catch (FileNotFoundException exception) {
            System.out.println("Error: dictionary file not found at " + DICTIONARY_FILE_PATH);
            System.exit(1);
        }

        return dictionary;
    }

    private static void startNewRound(String secretWord, Scanner inputScanner) {
        int wrongGuessCount = 0;
        Set<Character> guessedLetterSet = new HashSet<>();
        GameStatus status = GameStatus.IN_PROGRESS;

        while (status == GameStatus.IN_PROGRESS) {
            displayStatus(secretWord, guessedLetterSet, wrongGuessCount);
            Character guess = getUserGuess(inputScanner, guessedLetterSet);

            if (secretWord.contains(String.valueOf(guess))) {
                guessedLetterSet.add(guess);
            } else {
                wrongGuessCount++;
            }

            status = calculateGameStatus(secretWord, guessedLetterSet, wrongGuessCount);
            if (status == GameStatus.LOST) {
                displayHangman(wrongGuessCount);
                System.out.println(GameStatus.LOST);
                System.out.println("Secret word is " + secretWord);
                return;
            }
            if (status == GameStatus.WON) {
                displaySecretWord(secretWord, guessedLetterSet);
                System.out.println(GameStatus.WON);
                return;
            }
        }
    }

    private static GameStatus calculateGameStatus(String secretWord, Set<Character> guessedLetterSet,
            int wrongGuessCount) {
        if (wrongGuessCount == MAX_GUESSES)
            return GameStatus.LOST;
        for (Character character : secretWord.toCharArray()) {
            if (!guessedLetterSet.contains(character))
                return GameStatus.IN_PROGRESS;
        }
        return GameStatus.WON;
    }

    private static void displayStatus(String secretWord, Set<Character> guessedLetterSet, int wrongGuessCount) {
        displayHangman(wrongGuessCount);
        displaySecretWord(secretWord, guessedLetterSet);
    }

    private static void displaySecretWord(String secretWord, Set<Character> guessedLetterSet) {
        StringBuilder output = new StringBuilder();
        for (Character character : secretWord.toCharArray()) {
            output.append(guessedLetterSet.contains(character) ? character : "*");
        }
        System.out.println("Secret word: " + output);
    }

    private static void displayHangman(int wrongGuessCount) {
        System.out.println(buildHangmanFigure(wrongGuessCount));
    }

    private static String buildHangmanFigure(int wrongGuessCount) {
        StringBuilder hangman = new StringBuilder();

        hangman.append(" -------\n");
        hangman.append(" |     ").append(wrongGuessCount > 0 ? "O" : "").append("\n");
        hangman.append(" |    ")
                .append(wrongGuessCount > 1 ? "/" : "")
                .append(wrongGuessCount > 2 ? "|" : "")
                .append(wrongGuessCount > 3 ? "\\" : "").append("\n");
        hangman.append(" |    ")
                .append(wrongGuessCount > 4 ? "/ " : "")
                .append(wrongGuessCount > 5 ? "\\" : "").append("\n");
        hangman.append("/_\\");

        return hangman.toString();
    }

    private static Character getUserGuess(Scanner inputScanner, Set<Character> guessedLetterSet) {
        while (true) {
            System.out.print("Enter your guess: ");
            String input = inputScanner.nextLine().trim().toLowerCase();

            if (input.length() == 0) {
                System.out.println("Please enter a single letter.");
                continue;
            }

            Character guess = input.charAt(0);
            if (input.length() == 1 && Character.isLetter(guess)) {
                if (guessedLetterSet.contains(guess)) {
                    System.out.println("Letter already guessed.");
                    continue;
                }
                return guess;
            }

            System.out.println("Invalid input. Enter single letter only.");
        }
    }

    private static String generateSecretWord(List<String> dictionary) {
        Random random = new Random();

        if (dictionary.isEmpty())
            throw new IllegalStateException("Dictionary is empty; cannot select secret word.");
        return dictionary.get(random.nextInt(dictionary.size()));
    }

    private static boolean shouldStartNewRound(Scanner inputScanner) {
        while (true) {
            System.out.print("Start a new round? (Y/N): ");
            String input = inputScanner.nextLine().trim().toUpperCase();
            if (input.equals("Y"))
                return true;
            if (input.equals("N"))
                return false;
            System.out.println("Invalid input. Enter 'Y' for yes, and 'N' to exit.");
        }
    }

    private static enum GameStatus {
        IN_PROGRESS, WON, LOST;

        @Override
        public String toString() {
            switch (this) {
                case IN_PROGRESS:
                    return "In progress";
                case WON:
                    return "You won!";
                case LOST:
                    return "You lost!";
                default:
                    return "Undefined";
            }
        }
    }
}
