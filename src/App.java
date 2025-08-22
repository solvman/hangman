import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * A console-based Hangman game where players guess letters to reveal a secret
 * word.
 * The game loads words from a dictionary file, allows 6 incorrect guesses, and
 * supports multiple rounds.
 */
public class App {
    /** Maximum number of incorrect guesses allowed. */
    private static final Integer MAX_GUESSES = 6;
    /** Path to the dictionary file containing words. */
    private static final String DICTIONARY_FILE_PATH = "data/dictionary.txt";

    /**
     * Entry point for the Hangman game. Starts the game loop.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        startGame();
    }

    /**
     * Initializes the game, loads the dictionary, and manages game rounds.
     * Exits on dictionary loading errors.
     */
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

    /**
     * Loads words from the dictionary file into a list.
     * 
     * @return list of words in lowercase
     */
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

    /**
     * Starts a new round of the game with the given secret word.
     * Manages guesses and game status until win or loss.
     * 
     * @param secretWord   the word to guess
     * @param inputScanner scanner for user input
     */
    private static void startNewRound(String secretWord, Scanner inputScanner) {
        Integer incorrectGuessCount = 0;
        Set<Character> correctGuessLetterSet = new HashSet<>();
        Set<Character> incorrectGuessLetterSet = new HashSet<>();
        GameStatus status = GameStatus.IN_PROGRESS;

        while (status == GameStatus.IN_PROGRESS) {

            displayStatus(secretWord, correctGuessLetterSet, incorrectGuessLetterSet, incorrectGuessCount);
            Character guess = getUserGuess(inputScanner, correctGuessLetterSet, incorrectGuessLetterSet);

            if (secretWord.contains(String.valueOf(guess))) {
                correctGuessLetterSet.add(guess);
            } else {
                incorrectGuessLetterSet.add(guess);
                incorrectGuessCount = incorrectGuessLetterSet.size();
            }

            status = calculateGameStatus(secretWord, correctGuessLetterSet, incorrectGuessCount);
            if (status == GameStatus.LOST) {
                displayHangman(incorrectGuessCount);
                System.out.println(GameStatus.LOST);
                System.out.println("Secret word is " + secretWord);
                return;
            }
            if (status == GameStatus.WON) {
                displaySecretWord(secretWord, correctGuessLetterSet);
                System.out.println(GameStatus.WON);
                return;
            }
        }
    }

    /**
     * Calculates the current game status based on guesses and secret word.
     * 
     * @param secretWord            the word to guess
     * @param correctGuessLetterSet set of guessed letters
     * @param incorrectGuessCount   number of incorrect guesses
     * @return current game status (IN_PROGRESS, WON, LOST)
     */
    private static GameStatus calculateGameStatus(String secretWord, Set<Character> correctGuessLetterSet,
            Integer incorrectGuessCount) {
        if (incorrectGuessCount == MAX_GUESSES)
            return GameStatus.LOST;
        for (Character character : secretWord.toCharArray()) {
            if (!correctGuessLetterSet.contains(character))
                return GameStatus.IN_PROGRESS;
        }
        return GameStatus.WON;
    }

    /**
     * Displays the current game status, including hangman figure and secret word
     * progress.
     * 
     * @param secretWord              the word to guess
     * @param correctGuessLetterSet   set of guessed letters
     * @param incorrectGuessLetterSet set of incorrectly guessed letters
     * @param incorrectGuessCount     number of incorrect guesses
     */
    private static void displayStatus(String secretWord, Set<Character> correctGuessLetterSet,
            Set<Character> incorrectGuessLetterSet, Integer incorrectGuessCount) {
        displayHangman(incorrectGuessCount);
        displaySecretWord(secretWord, correctGuessLetterSet);
        displayWrongGuesses(incorrectGuessLetterSet);
    }

    private static void displayWrongGuesses(Set<Character> incorrectGuessLetterSet) {
        System.out.println("Incorrect guesses: " + incorrectGuessLetterSet);
    }

    /**
     * Displays the secret word with guessed letters revealed and unguessed letters
     * as asterisks.
     * 
     * @param secretWord            the word to guess
     * @param correctGuessLetterSet set of guessed letters
     */
    private static void displaySecretWord(String secretWord, Set<Character> correctGuessLetterSet) {
        StringBuilder output = new StringBuilder();
        for (Character character : secretWord.toCharArray()) {
            output.append(correctGuessLetterSet.contains(character) ? character : "*");
        }
        System.out.println("Secret word: " + output);
    }

    /**
     * Displays the hangman figure based on the number of incorrect guesses.
     * 
     * @param incorrectGuessCount number of incorrect guesses
     */
    private static void displayHangman(int incorrectGuessCount) {
        System.out.println(buildHangmanFigure(incorrectGuessCount));
    }

    /**
     * Builds the hangman figure as a string based on the number of incorrect
     * guesses.
     * 
     * @param incorrectGuessCount number of incorrect guesses
     * @return string representation of the hangman figure
     */
    private static String buildHangmanFigure(int incorrectGuessCount) {
        StringBuilder hangman = new StringBuilder();

        hangman.append(" -------\n");
        hangman.append(" |     ").append(incorrectGuessCount > 0 ? "O" : "").append("\n");
        hangman.append(" |    ")
                .append(incorrectGuessCount > 1 ? "/" : "")
                .append(incorrectGuessCount > 2 ? "|" : "")
                .append(incorrectGuessCount > 3 ? "\\" : "").append("\n");
        hangman.append(" |    ")
                .append(incorrectGuessCount > 4 ? "/ " : "")
                .append(incorrectGuessCount > 5 ? "\\" : "").append("\n");
        hangman.append("/_\\");

        return hangman.toString();
    }

    /**
     * Prompts the user for a single letter guess, validating input and checking for
     * duplicates.
     * 
     * @param inputScanner            scanner for user input
     * @param correctGuessLetterSet   set of already guessed letters
     * @param incorrectGuessLetterSet set of already incorrectly guessed letters
     * @return valid, unique letter guess
     */
    private static Character getUserGuess(Scanner inputScanner, Set<Character> correctGuessLetterSet,
            Set<Character> incorrectGuessLetterSet) {
        while (true) {
            System.out.print("Enter your guess: ");
            String input = inputScanner.nextLine().trim().toLowerCase();

            if (input.length() == 0) {
                System.out.println("Please enter a single letter.");
                continue;
            }

            Character guess = input.charAt(0);
            if (input.length() == 1 && Character.isLetter(guess) && ((guess >= 'a') && (guess <= 'z'))) {
                if (correctGuessLetterSet.contains(guess)) {
                    System.out.println("Letter already guessed.");
                    continue;
                }
                if (incorrectGuessLetterSet.contains(guess)) {
                    System.out.println("Letter already tried.");
                    continue;
                }
                return guess;
            }

            System.out.println("Invalid input. Enter single English letter only (a-z).");
        }
    }

    /**
     * Selects a random word from the dictionary.
     * 
     * @param dictionary list of words
     * @return a random word
     * @throws IllegalStateException if the dictionary is empty
     */
    private static String generateSecretWord(List<String> dictionary) {
        Random random = new Random();

        if (dictionary.isEmpty())
            throw new IllegalStateException("Dictionary is empty; cannot select secret word.");
        return dictionary.get(random.nextInt(dictionary.size()));
    }

    /**
     * Prompts the user to start a new round.
     * 
     * @param inputScanner scanner for user input
     * @return true if the user wants to start a new round, false otherwise
     */
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

    /**
     * Enum representing the game status.
     */
    private static enum GameStatus {
        IN_PROGRESS, WON, LOST;

        /**
         * Returns a string representation of the game status.
         * 
         * @return status description
         */
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
