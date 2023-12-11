package com.tcooling.wordle.parser

import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import cats.effect.IO
import cats.data.{NonEmptyList, NonEmptySet}
import com.tcooling.wordle.model.{Filename, WordLength, WordsParserError}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}

final class WordsParser(filename: Filename, wordLength: WordLength, fileReader: FileReader) {

  def parseWords: Either[WordsParserError, NonEmptySet[String]] = for {
    words         <- fileReader.getLines(filename).use(IO.pure).attempt.unsafeRunSync().leftMap(_ => FileParseError)
    nonEmptyWords <- NonEmptyList.fromList(words).map(_.toNes).toRight(EmptyFileError)
    wordsOfCorrectLength     <- filterIncorrectLength(nonEmptyWords)
    wordsWithoutSpecialChars <- filterNonLetterChars(wordsOfCorrectLength)
  } yield wordsWithoutSpecialChars.map(_.toUpperCase)

  private def filterIncorrectLength(words: NonEmptySet[String]): Either[WordsParserError, NonEmptySet[String]] =
    NonEmptySet.fromSet(words.filter(_.length == wordLength.value)).toRight(InvalidWordsError)

  private def filterNonLetterChars(words: NonEmptySet[String]): Either[WordsParserError, NonEmptySet[String]] =
    NonEmptySet
      .fromSet(words.filter(WordRegex.validate[Boolean](_, ifMatchesRegex = true, ifDoesNotMatchRegex = false)))
      .toRight(InvalidWordsError)

}
