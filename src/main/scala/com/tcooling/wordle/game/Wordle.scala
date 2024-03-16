package com.tcooling.wordle.game

import cats.data.NonEmptySet
import cats.effect.ExitCode
import com.tcooling.wordle.game.WordleFSM.State
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.FSM.{Exit, Start}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}
import com.tcooling.wordle.model.{FSM, TargetWord, WordGuess, WordleConfig, WordsParserError}
import com.tcooling.wordle.parser.{FileReader, WordsParser}

import scala.annotation.tailrec

trait Wordle[F[_]] {
  def startGame: F[ExitCode]
}

object Wordle {
  def apply[F[_]](
      config: WordleConfig,
      fileReader: FileReader[F],
      randomWord: NonEmptySet[String] => F[String],
      guessConnector: GuessInputConnector[F]
  ): Wordle[F] = new Wordle[F] {
    override def startGame: F[ExitCode] = {

      val linesR = fileReader.getLines(config.filename)
      val aaa = linesR.attempt

      ???
    }
  }
}

final class WordleOld[F[_]](
    config: WordleConfig,
    fileReader: FileReader[F],
    randomWord: NonEmptySet[String] => F[String],
    guessConnector: GuessInputConnector[F]
) {

  def startGame(): Unit =
    WordsParser.apply(config.filename, config.wordLength, fileReader).parseWords match {
      case Left(error) => printError(error)
      case Right(allWords) =>
        println(
          s"Successfully parsed ${config.filename}, read ${allWords.length} words of length ${config.wordLength.value}")
        val targetWord = TargetWord.apply(randomWord(allWords))
        gameLoop(config, targetWord, allWords, guessConnector)
    }

  private def gameLoop(
      config: WordleConfig,
      targetWord: TargetWord,
      allWords: NonEmptySet[String],
      guessConnector: GuessInputConnector[F]
  ): Unit = {
    val nextStateF: (FSM, List[WordGuess]) => (FSM, List[WordGuess]) =
      WordleFSM.nextState(config, targetWord, allWords, guessConnector)(_, _)

    @tailrec
    def loop(state: FSM, guesses: List[WordGuess]): State = state match {
      case Exit => Exit -> guesses
      case _ =>
        val (nextState, updatedGuesses) = nextStateF(state, guesses)
        loop(nextState, updatedGuesses)
    }

    loop(state = Start, guesses = Nil)
  }

  private def printError(error: WordsParserError): Unit = error match {
    case FileParseError    => println("Error parsing words file")
    case InvalidWordsError => println("Error parsing words (possibly word length or special characters)")
    case EmptyFileError    => println("Empty words file error")
  }

}
