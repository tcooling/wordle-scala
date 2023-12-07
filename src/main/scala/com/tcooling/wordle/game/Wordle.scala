package com.tcooling.wordle.game

import cats.data.NonEmptySet
import com.tcooling.wordle.game.WordleFSM.State
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.FSM.{Exit, Start}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}
import com.tcooling.wordle.model.{FSM, WordGuess, WordleConfig, WordsParserError}
import com.tcooling.wordle.parser.{FileReader, WordsParser}

import scala.annotation.tailrec

final class Wordle(
    config: WordleConfig,
    fileReader: FileReader,
    randomWord: NonEmptySet[String] => String,
    guessConnector: GuessInputConnector
) {

  def startGame(): Unit =
    WordsParser.apply(config.filename, config.wordLength, fileReader).parseWords match {
      case Left(error) => printError(error)
      case Right(allWords) =>
        println(
          s"Successfully parsed ${config.filename}, read ${allWords.length} words of length ${config.wordLength.value}")
        val targetWord = randomWord(allWords)
        gameLoop(config, targetWord, allWords, guessConnector)
    }

  private def gameLoop(
      config: WordleConfig,
      targetWord: String,
      allWords: NonEmptySet[String],
      guessConnector: GuessInputConnector
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
