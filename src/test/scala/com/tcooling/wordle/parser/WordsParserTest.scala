package com.tcooling.wordle.parser

import cats.data.NonEmptySet
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}

import scala.util.{Failure, Success}

final class WordsParserTest extends WordSpecLike with Matchers with MockFactory {

  private val fileReader: FileReader = mock[FileReader]
  private val filename:   String     = "someWordsFile.txt"
  private val wordLength: Int        = 5

  "WordsParser" should {
    "parse valid words input" when {
      "there is only one word" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List("hello")))
        parser.parseWords shouldBe Right(NonEmptySet.of("HELLO"))
      }

      "there are many words" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List("hello", "world", "horse", "robot")))
        parser.parseWords shouldBe Right(NonEmptySet.of("HELLO", "WORLD", "HORSE", "ROBOT"))
      }

      "some words are capitalised" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List("hello", "WORLD", "HORSE", "robot")))
        parser.parseWords shouldBe Right(NonEmptySet.of("HELLO", "WORLD", "HORSE", "ROBOT"))
      }

      "some words are of incorrect length" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List("hell", "world", "horse", "helloo")))
        parser.parseWords shouldBe Right(NonEmptySet.of("WORLD", "HORSE"))
      }

      "some words have non letter characters" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List("hell-", "world", "horse", "hell&")))
        parser.parseWords shouldBe Right(NonEmptySet.of("WORLD", "HORSE"))
      }
    }

    "return an error" when {
      "the file reader cannot read the file" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Failure(new RuntimeException("Cannot parse file")))
        parser.parseWords shouldBe Left(FileParseError)
      }

      "no words are present" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List()))
        parser.parseWords shouldBe Left(EmptyFileError)
      }

      "all the words are of incorrect length" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List("h", "he", "hell", "helloo")))
        parser.parseWords shouldBe Left(InvalidWordsError)
      }

      "all the words include non letter characters" in {
        val parser = WordsParser(filename, wordLength, fileReader)
        (fileReader.getLines _).expects(filename).returning(Success(List("12345", "hell&", "^^", "hell0")))
        parser.parseWords shouldBe Left(InvalidWordsError)
      }
    }
  }

}
