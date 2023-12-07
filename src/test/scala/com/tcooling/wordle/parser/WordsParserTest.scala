package com.tcooling.wordle.parser

import cats.data.NonEmptySet
import com.tcooling.wordle.model.{Filename, WordLength}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success, Try}

final class WordsParserTest extends AnyWordSpecLike with Matchers {

  private val filename: Filename = Filename.apply("someWordsFile.txt")

  @SuppressWarnings(Array("DisableSyntax.throw"))
  private def fileReader(expectedFilename: Filename, linesTry: Try[List[String]]): FileReader = (filename: Filename) =>
    if (filename == expectedFilename) {
      linesTry
    } else throw new RuntimeException("Filename does not match")

  private val fileReaderF: Try[List[String]] => FileReader = fileReader(filename, _)
  private val wordLength: WordLength                       = WordLength.apply(5)

  private def wordsParser(linesTry: Try[List[String]]): WordsParser =
    WordsParser(filename, wordLength, fileReaderF(linesTry))

  "WordsParser" should {
    "parse valid words input" when {
      "there is only one word" in {
        wordsParser(Success(List("hello"))).parseWords shouldBe Right(NonEmptySet.of("HELLO"))
      }

      "there are many words" in {
        wordsParser(Success(List("hello", "world", "horse", "robot"))).parseWords shouldBe
          Right(NonEmptySet.of("HELLO", "WORLD", "HORSE", "ROBOT"))
      }

      "some words are capitalised" in {
        wordsParser(Success(List("hello", "WORLD", "HORSE", "robot"))).parseWords shouldBe
          Right(NonEmptySet.of("HELLO", "WORLD", "HORSE", "ROBOT"))
      }

      "some words are of incorrect length" in {
        wordsParser(Success(List("hell", "world", "horse", "helloo"))).parseWords shouldBe
          Right(NonEmptySet.of("WORLD", "HORSE"))
      }

      "some words have non letter characters" in {
        wordsParser(Success(List("hell-", "world", "horse", "hell&"))).parseWords shouldBe
          Right(NonEmptySet.of("WORLD", "HORSE"))
      }
    }

    "return an error" when {
      "the file reader cannot read the file" in {
        wordsParser(Failure(new RuntimeException("Cannot parse file"))).parseWords shouldBe Left(FileParseError)
      }

      "no words are present" in {
        wordsParser(Success(List())).parseWords shouldBe Left(EmptyFileError)
      }

      "all the words are of incorrect length" in {
        wordsParser(Success(List("h", "he", "hell", "helloo"))).parseWords shouldBe Left(InvalidWordsError)
      }

      "all the words include non letter characters" in {
        wordsParser(Success(List("12345", "hell&", "^^", "hell0"))).parseWords shouldBe Left(InvalidWordsError)
      }
    }
  }
}
