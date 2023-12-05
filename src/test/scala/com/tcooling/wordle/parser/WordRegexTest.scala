package com.tcooling.wordle.parser

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class WordRegexTest extends AnyWordSpecLike with Matchers {

  private val onSuccess: String = "onSuccess"
  private val onFailure: String = "onFailure"

  private val validateWordF: String => String = WordRegex.validate[String](_, onSuccess, onFailure)

  "WordRegex" should {
    "parse a valid word" when {
      val validWords: Set[String] = Set("hello", "HELLO", "HELlo")

      validWords.foreach { word =>
        s"when the word is '$word'" in {
          validateWordF(word) shouldBe onSuccess
        }
      }
    }

    "not parse an valid word" when {
      val invalidWords: Set[String] = Set("     ", "12345", "-----", "[][][")

      invalidWords.foreach { word =>
        s"when the word is '$word'" in {
          validateWordF(word) shouldBe onFailure
        }
      }
    }
  }

}
