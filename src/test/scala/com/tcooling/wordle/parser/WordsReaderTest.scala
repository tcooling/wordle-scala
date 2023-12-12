package com.tcooling.wordle.parser

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.IO
import org.scalatest.wordspec.AsyncWordSpec
import com.tcooling.wordle.model.Filename
import org.scalatest.matchers.should.Matchers

final class WordsReaderTest extends AsyncWordSpec with AsyncIOSpec with Matchers {

  "WordsReader" should {
    "read a valid file and return a list of lines" in {
      (for {
        lines <- WordsReader.getLines(filename = Filename.apply("validFile.txt")).use(IO.pure)
      } yield lines shouldBe List("hello", "world")).unsafeRunSync()
    }

    "raise an exception for a file that does not exist" in {
      val lines: IO[List[String]] = WordsReader.getLines(filename = Filename.apply("fileDoesNotExist.txt")).use(IO.pure)
      val linesOrError: IO[Either[Throwable, List[String]]] = lines.attempt
      linesOrError.map {
        case Left(err)    => err.isInstanceOf[java.io.FileNotFoundException] shouldBe true
        case Right(lines) => fail()
      }
    }
  }
}
