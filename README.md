# Hangman Game

## Overview

A console-based Hangman game implemented in Java. Players guess letters to reveal a secret word, with a limit of 6 incorrect guesses before losing. The game supports multiple rounds and loads words from a dictionary file.

## Features

- Loads words from `data/dictionary.txt`.
- Displays hangman figure and secret word progress.
- Validates user input (single letter, no duplicates).
- Tracks game status (in progress, won, lost).
- Handles errors (e.g., missing dictionary file).

## Requirements

- Java 8 or higher.
- A `dictionary.txt` file in the `data/` directory, with one word per line.

## Setup

1. Ensure `data/dictionary.txt` exists with valid words.
2. Compile the code:

   ```bash
   javac App.java
   ```
3. Run the game:

   ```bash
   java App
   ```

## How to Play

1. Start the game; it loads words from `dictionary.txt`.
2. A secret word is chosen, shown as asterisks (`*`).
3. Enter a single letter to guess.
4. Correct guesses reveal letters; incorrect guesses add to the hangman figure.
5. Win by guessing the word within 6 incorrect guesses, or lose if the limit is reached.
6. Choose to play another round (Y/N).

## Error Handling

- Exits with a message if `dictionary.txt` is missing or empty.
- Validates input to ensure single letters and no repeated guesses.

## Project Structure

- `App.java`: Main game logic, including word selection, game loop, and display.
- `data/dictionary.txt`: Text file containing words (one per line).

## License

MIT License. Feel free to use and modify.