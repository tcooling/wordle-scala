package com.tcooling.wordle.parser

import cats.data.NonEmptySet
import cats.effect.{IO, Resource}
import com.tcooling.wordle.model.{Filename, WordLength}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class WordsParserTest extends AnyWordSpecLike with Matchers {

  private val filename: Filename = Filename.apply("someWordsFile.txt")

  @SuppressWarnings(Array("DisableSyntax.throw"))
  private def fileReader(expectedFilename: Filename, exceptionOrLines: Either[Exception, List[String]]): FileReader =
    new FileReader {
      override def getLines(filename: Filename): Resource[IO, List[String]] =
        if (filename == expectedFilename) Resource.pure(exceptionOrLines match {
          case Left(exception) => throw exception
          case Right(lines)    => lines
        })
        else throw new RuntimeException("Filename does not match")
    }

  private val fileReaderF: Either[Exception, List[String]] => FileReader = fileReader(filename, _)
  private val wordLength: WordLength                                     = WordLength.apply(5)

  private def wordsParser(linesTry: Either[Exception, List[String]]): WordsParser =
    WordsParser(filename, wordLength, fileReaderF(linesTry))

  "WordsParser" should {
    "parse valid words input" when {
      "there is only one word" in {
        wordsParser(Right(List("hello"))).parseWords shouldBe Right(NonEmptySet.of("HELLO"))
      }

      "there are many words" in {
        wordsParser(Right(List("hello", "world", "horse", "robot"))).parseWords shouldBe
          Right(NonEmptySet.of("HELLO", "WORLD", "HORSE", "ROBOT"))
      }

      "some words are capitalised" in {
        wordsParser(Right(List("hello", "WORLD", "HORSE", "robot"))).parseWords shouldBe
          Right(NonEmptySet.of("HELLO", "WORLD", "HORSE", "ROBOT"))
      }

      "some words are of incorrect length" in {
        wordsParser(Right(List("hell", "world", "horse", "helloo"))).parseWords shouldBe
          Right(NonEmptySet.of("WORLD", "HORSE"))
      }

      "some words have non letter characters" in {
        wordsParser(Right(List("hell-", "world", "horse", "hell&"))).parseWords shouldBe
          Right(NonEmptySet.of("WORLD", "HORSE"))
      }
    }

    "return an error" when {
      "the file reader cannot read the file" in {
        wordsParser(Left(new RuntimeException("Cannot parse file"))).parseWords shouldBe Left(FileParseError)
      }

      "no words are present" in {
        wordsParser(Right(List())).parseWords shouldBe Left(EmptyFileError)
      }

      "all the words are of incorrect length" in {
        wordsParser(Right(List("h", "he", "hell", "helloo"))).parseWords shouldBe Left(InvalidWordsError)
      }

      "all the words include non letter characters" in {
        wordsParser(Right(List("12345", "hell&", "^^", "hell0"))).parseWords shouldBe Left(InvalidWordsError)
      }
    }
  }
}
