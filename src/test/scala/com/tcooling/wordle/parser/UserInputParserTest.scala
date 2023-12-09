package com.tcooling.wordle.parser

import cats.data.{NonEmptyList, NonEmptySet}
import com.tcooling.wordle.model.{UserInputError, WordLength}
import com.tcooling.wordle.model.UserInputError.{IncorrectLength, NonLetterCharacter, WordDoesNotExist}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class UserInputParserTest extends AnyWordSpecLike with Matchers {

  private val wordLength: WordLength        = WordLength.apply(5)
  private val allWords: NonEmptySet[String] = NonEmptyList.of(head = "VAGUE", tail = "CLICK").toNes

  private val parseGuessF: String => Either[UserInputError, String] =
    UserInputParser.parseGuess(allWords, _, wordLength)

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
          parseGuessF("BOTTLE") shouldBe Left(IncorrectLength(wordLength.value))
        }

        "too short" in {
          parseGuessF("THE") shouldBe Left(IncorrectLength(wordLength.value))
        }

        "empty" in {
          parseGuessF("") shouldBe Left(IncorrectLength(wordLength.value))
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
