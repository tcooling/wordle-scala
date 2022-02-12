package com.tcooling.wordle.game

import cats.data.NonEmptySet
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}
import com.tcooling.wordle.model.{WordleConfig, WordsParserError}
import com.tcooling.wordle.parser.{FileReader, WordsParser}

final class Wordle(config: WordleConfig, fileReader: FileReader, randomWord: NonEmptySet[String] => String, guessConnector: GuessInputConnector) {

  def startGame(): Unit =
    WordsParser.apply(config.filename, config.wordLength, fileReader).parseWords match {
      case Left(error) => printError(error)
      case Right(allWords) =>
        println(s"Successfully parsed ${config.filename}, read ${allWords.length} words of length ${config.wordLength}")
        val targetWord = randomWord(allWords)
        GameLogic(targetWord, config, allWords, guessConnector).mainLoop()
    }

  private def printError(error: WordsParserError): Unit = error match {
    case FileParseError    => println("Error parsing words file")
    case InvalidWordsError => println("Error parsing words (possibly word length or special characters)")
    case EmptyFileError    => println("Empty words file error")
  }

}
