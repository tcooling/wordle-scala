package com.tcooling.wordle.parser

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

import scala.util.Success

final class WordsReaderTest extends AnyWordSpecLike with Matchers {

  "WordsReader" should {
    "read a valid file and return a list of lines" in {
      WordsReader.getLines(filename = "validFile.txt") shouldBe Success(
        List(
          "hello",
          "world"
        )
      )
    }

    "throw an exception for a file that does not exist" in {
      WordsReader.getLines(filename = "fileDoesNotExist.txt").isFailure shouldBe true
    }
  }

}
