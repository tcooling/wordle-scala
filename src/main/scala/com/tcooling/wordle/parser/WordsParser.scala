package com.tcooling.wordle.parser

import cats.data.{NonEmptyList, NonEmptySet}
import com.tcooling.wordle.model.WordsParserError
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}

object WordsParser {
  def apply(filename: String, wordLength: Int, fileReader: FileReader): WordsParser =
    new WordsParser(filename, wordLength, fileReader)
}

final class WordsParser private (filename: String, wordLength: Int, fileReader: FileReader) {

  def parseWords: Either[WordsParserError, NonEmptySet[String]] = for {
    words                    <- fileReader.getLines(filename).toOption.toRight(FileParseError)
    nonEmptyWords            <- NonEmptyList.fromList(words).map(_.toNes).toRight(EmptyFileError)
    wordsOfCorrectLength     <- filterIncorrectLength(nonEmptyWords)
    wordsWithoutSpecialChars <- filterNonLetterChars(wordsOfCorrectLength)
  } yield wordsWithoutSpecialChars.map(_.toUpperCase)

  private def filterIncorrectLength(words: NonEmptySet[String]): Either[WordsParserError, NonEmptySet[String]] =
    NonEmptySet.fromSet(words.filter(_.length == wordLength)).toRight(InvalidWordsError)

  private def filterNonLetterChars(words: NonEmptySet[String]): Either[WordsParserError, NonEmptySet[String]] =
    NonEmptySet
      .fromSet(words.filter(WordRegex.validate[Boolean](_, ifMatchesRegex = true, ifDoesNotMatchRegex = false)))
      .toRight(InvalidWordsError)

}
