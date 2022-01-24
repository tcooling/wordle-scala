package com.tcooling.wordle.game

import cats.data.NonEmptySet
import com.tcooling.wordle.game.GameBoard.printGameBoard
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.LetterGuess.{CorrectGuess, IncorrectGuess, WrongPositionGuess}
import com.tcooling.wordle.model.UserInputError.{IncorrectLength, NonLetterCharacter, WordDoesNotExist}
import com.tcooling.wordle.model.{GameState, UserInputError, WordGuess, WordleConfig}
import com.tcooling.wordle.parser.WordRegex

import scala.annotation.tailrec

object GameLogic {

  def apply(
    targetWord:          String,
    wordleConfig:        WordleConfig,
    allWords:            NonEmptySet[String],
    guessInputConnector: GuessInputConnector
  ): GameLogic =
    new GameLogic(targetWord, wordleConfig, allWords, guessInputConnector)
}

final class GameLogic(targetWord: String, config: WordleConfig, allWords: NonEmptySet[String], guessInputConnector: GuessInputConnector) {

  def mainLoop(): Unit = {
    printHelp()
    val initialState = GameState(previousGuesses = Nil, numberOfGuessesRemaining = config.numberOfGuesses, targetWord)

    @tailrec
    def loop(gameState: GameState): Unit = {
      printGameBoard(GameBoard.generateGameBoard(config, gameState.previousGuesses))
      if (gameState.mostRecentGuess.contains(targetWord)) {
        println(s"Well done! Number of guesses used: ${config.numberOfGuesses - gameState.numberOfGuessesRemaining}")
      } else if (gameState.numberOfGuessesRemaining == 0) {
        println(s"You failed to guess the word, the word was $targetWord")
      } else {
        println("Guess: ")
        val guess = guessInputConnector.getUserInput(gameState)
        loop(userInputToGameState(guess, gameState))
      }
    }

    loop(initialState)
  }

  /**
   * Using an invalidated user guess, create the resulting game state
   * - if the guess is invalid, print a message and return the unchanged game state
   * - if the guess is valid, add the guess to the game state and decrease the number of guesses counter
   */
  private def userInputToGameState(guess: String, gameState: GameState): GameState =
    validateUserInput(guess, config.wordLength) match {
      case Left(err) =>
        println(Console.BLUE + err.errorMessage + Console.RESET + "\nPlease try again.")
        gameState
      case Right(validUserInput) =>
        gameState.copy(
          previousGuesses = gameState.previousGuesses :+ createWordGuess(validUserInput, targetWord),
          numberOfGuessesRemaining = gameState.numberOfGuessesRemaining - 1
        )
    }

  /**
   * Validate that the user input is of the correct length, only contains valid characters and is a valid word.
   */
  private def validateUserInput(userInput: String, wordLength: Int): Either[UserInputError, String] = for {
    _ <- if (userInput.length == wordLength) Right(userInput) else Left(IncorrectLength(wordLength))
    guess <- WordRegex.validate[Either[UserInputError, String]](
               word = userInput,
               ifMatchesRegex = Right(userInput.toUpperCase),
               ifDoesNotMatchRegex = Left(NonLetterCharacter)
             )
    validGuess <- if (allWords.contains(guess)) Right(guess) else Left(WordDoesNotExist)
  } yield validGuess

  /**
   * From the user input word guess, create the [[WordGuess]] model. At this stage we have validated the length
   * of the target word and length of the guess so they can be zipped up and compared.
   */
  private def createWordGuess(userInput: String, targetWord: String): WordGuess =
    WordGuess(userInput.zip(targetWord).toList.map {
      case (letterGuess, target) if letterGuess == target       => CorrectGuess(letterGuess)
      case (letterGuess, _) if targetWord.contains(letterGuess) => WrongPositionGuess(letterGuess)
      case (letterGuess, _)                                     => IncorrectGuess(letterGuess)
    })

  private def printHelp(): Unit = {
    val separator = List.fill(100)("-").mkString
    println(separator)
    println(s"Guess the WORDLE in ${config.numberOfGuesses} tries.")
    println(s"Each guess must be a valid ${config.wordLength} letter word. Hit the enter button to submit.")
    println("After each guess, the color of the tiles will change to show how close your guess was to the word.")
    println("Examples")
    println(
      GameBoard.generateBoardRow(
        config,
        WordGuess(
          List(
            CorrectGuess('W'),
            IncorrectGuess('E'),
            IncorrectGuess('A'),
            IncorrectGuess('R'),
            IncorrectGuess('Y')
          )
        )
      )
    )
    println("The letter W is in the word and in the correct spot.")
    println(
      GameBoard.generateBoardRow(
        config,
        WordGuess(
          List(
            IncorrectGuess('P'),
            WrongPositionGuess('I'),
            IncorrectGuess('L'),
            IncorrectGuess('L'),
            IncorrectGuess('S')
          )
        )
      )
    )
    println("The letter I is in the word but in the wrong spot.")
    println(GameBoard.generateBoardRow(config, WordGuess("VAGUE".map(IncorrectGuess).toList)))
    println("None of the letters are in the word in any spot.")
    println(separator)
  }

}
