package com.tcooling.wordle.game

import cats.{Monad, Parallel}
import cats.data.NonEmptySet
import cats.syntax.all.*
import cats.implicits.*
import cats.effect.std.Console
import cats.effect.{ExitCode, Resource}
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
  def apply[F[_] : Monad : Parallel : Console](
      config: WordleConfig,
      fileReader: FileReader[F],
      randomWord: NonEmptySet[String] => F[String],
      guessConnector: GuessInputConnector[F]
  ): Wordle[F] = new Wordle[F] {

    override def startGame: F[ExitCode] = {

      val linesR: Resource[F, List[String]] = fileReader.getLines(config.filename)
      val aaa                               = linesR.use(x => parse(x))

      // TODO: use observed method for logging in here?
      val parser = WordsParser.apply[F](config, fileReader)
      parser.parseWords() map {
        case Left(err)    =>
        case Right(lines) =>
      }

      ???
    }
  }

  private def parse(words: List[String]): F[List[String]] = ???
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

  // TODO: use Concolse.errorLine here
  private def printError(error: WordsParserError): Unit = error match {
    case FileParseError    => println("Error parsing words file")
    case InvalidWordsError => println("Error parsing words (possibly word length or special characters)")
    case EmptyFileError    => println("Empty words file error")
  }

}
