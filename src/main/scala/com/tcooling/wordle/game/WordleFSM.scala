package com.tcooling.wordle.game

import cats.data.NonEmptySet
import cats.implicits.toShow
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.FSM.*
import com.tcooling.wordle.model.LetterGuess.{CorrectGuess, IncorrectGuess, WrongPositionGuess}
import com.tcooling.wordle.model.WordGuess.showWordGuess
import com.tcooling.wordle.model.{boardRow, FSM, NumberOfGuesses, WordGuess, WordLength, WordleConfig}
import com.tcooling.wordle.parser.UserInputParser

object WordleFSM {

  /**
   * Each state of the FSM will return a new FSM state and the word guesses
   */
  type State = (FSM, List[WordGuess])

  /**
   * Given a current FSM state and a list of guesses, a new FSM state and updated list of guesses will be returned
   */
  def nextState(
      config: WordleConfig,
      targetWord: String,
      allWords: NonEmptySet[String],
      guessConnector: GuessInputConnector
  )(state: FSM, guesses: List[WordGuess]): State = state match {
    case Start             => PrintHelp -> Nil
    case PrintHelp         => printHelp(config)
    case PrintGameBoard    => printGameBoard(guesses, config)
    case CheckForWinOrLoss => checkForWinOrLoss(targetWord, config.numberOfGuesses, guesses)
    case UserInputGuess    => userInputGuess(allWords, guessConnector, config.wordLength, targetWord, guesses)
    case Win               => win(guesses)
    case Lose              => lose(targetWord, guesses)
    case Exit              => Exit      -> guesses
  }

  private def printGameBoard(guesses: List[WordGuess], config: WordleConfig): State = {
    val gameBoardRows = GameBoard.generateGameBoard(config.wordLength, config.numberOfGuesses, guesses)
    println(gameBoardRows.mkString("\n"))
    CheckForWinOrLoss -> guesses
  }

  private def win(guesses: List[WordGuess]): State = {
    println(s"Well done! Number of guesses used: ${guesses.length}")
    Exit -> guesses
  }

  private def lose(targetWord: String, guesses: List[WordGuess]): State = {
    println(s"You failed to guess the word, the word was $targetWord")
    Exit -> guesses
  }

  private def checkForWinOrLoss(targetWord: String, numberOfGuesses: NumberOfGuesses, guesses: List[WordGuess]): State =
    if (guesses.lastOption.map(_.show).contains(targetWord)) Win -> guesses
    else if (guesses.length == numberOfGuesses.value) Lose -> guesses
    else UserInputGuess                                    -> guesses

  private def userInputGuess(
      allWords: NonEmptySet[String],
      guessConnector: GuessInputConnector,
      wordLength: WordLength,
      targetWord: String,
      guesses: List[WordGuess]
  ): State = {
    println("Guess: ")
    val guess = guessConnector.getUserInput
    val updatedGuesses = UserInputParser.parseGuess(allWords, guess, wordLength) match {
      case Left(err) =>
        println(err.show + "\nPlease try again.")
        guesses
      case Right(validUserInput) => guesses :+ WordGuess(validUserInput, targetWord)
    }
    PrintGameBoard -> updatedGuesses
  }

  private def printHelp(config: WordleConfig): State = {
    val separator = List.fill(100)("-").mkString
    println(separator)
    println(s"Guess the WORDLE in ${config.numberOfGuesses} tries.")
    println(s"Each guess must be a valid ${config.wordLength} letter word. Hit the enter button to submit.")
    println("After each guess, the color of the tiles will change to show how close your guess was to the word.")
    println("Examples")
    println(
      WordGuess(
        List(
          CorrectGuess('W'),
          IncorrectGuess('E'),
          IncorrectGuess('A'),
          IncorrectGuess('R'),
          IncorrectGuess('Y')
        )
      ).boardRow
    )
    println("The letter W is in the word and in the correct spot.")
    println(
      WordGuess(
        List(
          IncorrectGuess('P'),
          WrongPositionGuess('I'),
          IncorrectGuess('L'),
          IncorrectGuess('L'),
          IncorrectGuess('S')
        )
      ).boardRow
    )
    println("The letter I is in the word but in the wrong spot.")
    println(WordGuess("VAGUE".map(IncorrectGuess.apply).toList).boardRow)
    println("None of the letters are in the word in any spot.")
    println(separator)
    PrintGameBoard -> Nil
  }

}
