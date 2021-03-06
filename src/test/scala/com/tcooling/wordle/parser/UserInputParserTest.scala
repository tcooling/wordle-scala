package com.tcooling.wordle.parser

import cats.data.{NonEmptyList, NonEmptySet}
import com.tcooling.wordle.model.UserInputError
import com.tcooling.wordle.model.UserInputError.{IncorrectLength, NonLetterCharacter, WordDoesNotExist}
import org.scalatest.{Matchers, WordSpecLike}

final class UserInputParserTest extends WordSpecLike with Matchers {

  private val wordLength: Int                 = 5
  private val allWords:   NonEmptySet[String] = NonEmptyList.of(head = "VAGUE", tail = "CLICK").toNes

  private val parseGuessF: String => Either[UserInputError, String] = UserInputParser.parseGuess(allWords, _, wordLength)

  "UserInputParser" should {
    "parse valid user input" when {
      "user input is already capitalised" in {
        parseGuessF("VAGUE") shouldBe Right("VAGUE")
      }

      "user input is in lower case" in {
        parseGuessF("vague") shouldBe Right("VAGUE")
      }
    }

    "parse invalid user input" when {
      "the length is not equal to 5" when {
        "too long" in {
          parseGuessF("BOTTLE") shouldBe Left(IncorrectLength(wordLength))
        }

        "too short" in {
          parseGuessF("THE") shouldBe Left(IncorrectLength(wordLength))
        }

        "empty" in {
          parseGuessF("") shouldBe Left(IncorrectLength(wordLength))
        }
      }

      "it does not match the regex" when {
        "it includes special characters" in {
          parseGuessF("&&&&&") shouldBe Left(NonLetterCharacter)
        }

        "it includes numbers" in {
          parseGuessF("12345") shouldBe Left(NonLetterCharacter)
        }
      }

      "the word does not exist in the set of valid words" in {
        parseGuessF("HELLO") shouldBe Left(WordDoesNotExist)
      }
    }
  }

}
