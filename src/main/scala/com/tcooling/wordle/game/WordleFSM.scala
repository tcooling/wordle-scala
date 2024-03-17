package com.tcooling.wordle.game

import cats.{Applicative, Monad}
import cats.data.NonEmptySet
import cats.effect.std.Console
import cats.implicits.toShow
import cats.syntax.all.*
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.FSM.*
import com.tcooling.wordle.model.LetterGuess.{Correct, Incorrect, WrongPosition}
import com.tcooling.wordle.model.WordGuess.showWordGuess
import com.tcooling.wordle.game.WordleFSM.State
import com.tcooling.wordle.model.{boardRow, FSM, NumberOfGuesses, TargetWord, WordGuess, WordLength, WordleConfig}
import com.tcooling.wordle.parser.UserInputParser

// TODO: double check cats implicits imports
trait WordleFSM[F[_]] {

  /**
   * Given a current FSM state and a list of guesses, a new FSM state and updated list of guesses will be returned TODO:
   * would prefer not to curry in allWords and targetWord here, but have to given how the live methods work, e.g. do not
   * want to run parseWords logic in live method?
   */
  def nextState(allWords: NonEmptySet[String], targetWord: TargetWord)(state: FSM, guesses: List[WordGuess]): F[State]
}

object WordleFSM {

  /**
   * Each state of the FSM will return a new FSM state and the word guesses
   */
  type State = (FSM, List[WordGuess])

  def apply[F[_] : Console : Applicative : Monad](
      config: WordleConfig,
      guessConnector: GuessInputConnector[F]
  ): WordleFSM[F] = new WordleFSM[F] {

    override def nextState(allWords: NonEmptySet[String],
                           targetWord: TargetWord)(state: FSM, guesses: List[WordGuess]): F[State] = state match {
      case Start             => (PrintHelp -> Nil).pure
      case PrintHelp         => printHelp
      case PrintGameBoard    => printGameBoard(guesses)
      case CheckForWinOrLoss => checkForWinOrLoss(targetWord, guesses)
      case UserInputGuess    => userInputGuess(allWords, targetWord, guesses)
      case Win               => win(guesses)
      case Lose              => lose(targetWord, guesses)
      case Exit              => (Exit      -> guesses).pure
    }

    private def userInputGuess(allWords: NonEmptySet[String],
                               targetWord: TargetWord,
                               guesses: List[WordGuess]): F[State] =
      for {
        _     <- Console[F].println("Guess: ")
        guess <- guessConnector.getUserInput // TODO: is console handling IO.delay etc?
        updatedGuesses <- UserInputParser.parseGuess(allWords, guess, config.wordLength) match {
          case Left(err) => List(err.show, "Please try again.").traverse(Console[F].println).void.as(guesses)
          case Right(validUserInput) => (guesses :+ WordGuess(validUserInput, targetWord)).pure
        }
      } yield PrintGameBoard -> updatedGuesses

    private def printGameBoard(guesses: List[WordGuess]): F[State] =
      GameBoard
        .generateGameBoard(config.wordLength, config.numberOfGuesses, guesses)
        .traverse(Console[F].println)
        .void
        .as(CheckForWinOrLoss -> guesses)

    private def checkForWinOrLoss(targetWord: TargetWord, guesses: List[WordGuess]): F[State] = {
      val nextFsm =
        if (guesses.lastOption.map(_.show).contains(targetWord.value)) Win
        else if (guesses.length == config.numberOfGuesses.value) Lose
        else UserInputGuess
      (nextFsm -> guesses).pure
    }

    private def win(guesses: List[WordGuess]): F[State] =
      Console[F].println(s"Well done! Number of guesses used: ${guesses.length}").as(Exit -> guesses)

    private def lose(targetWord: TargetWord, guesses: List[WordGuess]): F[State] =
      Console[F].println(s"You failed to guess the word, the word was ${targetWord.value}").as(Exit -> guesses)

    private def printHelp: F[State] = {
      val separator = List.fill(100)("-").mkString
      val linesToPrint = List(
        separator,
        s"Guess the WORDLE in ${config.numberOfGuesses.value} tries.",
        s"Each guess must be a valid ${config.wordLength.value} letter word. Hit the enter button to submit.",
        "After each guess, the color of the tiles will change to show how close your guess was to the word.",
        "Examples",
        WordGuess(List(Correct('W'), Incorrect('E'), Incorrect('A'), Incorrect('R'), Incorrect('Y'))).boardRow,
        "The letter W is in the word and in the correct spot.",
        WordGuess(List(Incorrect('P'), WrongPosition('I'), Incorrect('L'), Incorrect('L'), Incorrect('S'))).boardRow,
        "The letter I is in the word but in the wrong spot.",
        WordGuess("VAGUE".map(Incorrect.apply).toList).boardRow,
        "None of the letters are in the word in any spot.",
        separator
      )

      linesToPrint.traverse(Console[F].println).void.as(PrintGameBoard -> Nil)
    }

  }
}
