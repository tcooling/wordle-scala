package com.tcooling.wordle.parser

import cats.effect.IO
import cats.syntax.all.*
import cats.effect.Resource
import com.tcooling.wordle.model.Filename
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

import scala.util.Success

final class WordsReaderTest extends AnyWordSpecLike with Matchers {

  "WordsReader" should {
    "read a valid file and return a list of lines" in {
      val res = WordsReader.getLines(filename = Filename.apply("validFile.txt")).use(IO.pure).unsafeRunSync()

      shouldBe Success (
        List(
          "hello",
          "world"
        )
      )
    }

    "throw an exception for a file that does not exist" in {
      WordsReader.getLines(filename = Filename.apply("fileDoesNotExist.txt")).isFailure shouldBe true
    }
  }
}
