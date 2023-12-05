package com.tcooling.wordle.game

import cats.data.NonEmptySet
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.WordleConfig
import com.tcooling.wordle.parser.{FileReader, WordsReader}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class WordleTest extends AnyWordSpecLike with Matchers {

  private val targetWord: String = "VAGUE"
  private val config: WordleConfig = WordleConfig(
    filename = "words.txt",
    wordLength = 5,
    numberOfGuesses = 6
  )
  private val fileReader:        FileReader                    = WordsReader
  private val chooseRandomWordF: NonEmptySet[String] => String = _ => targetWord

  /**
   * An out of bounds exception will be thrown if the next index in the list does not exist, meaning the tests will
   * fail if the [[GuessInputConnector]] is called when not supposed to be called. Basically, asserting that the FSM
   * reaches the end state (no more guesses are requested for user input).
   */
  private def guessConnector(guesses: List[String]): GuessInputConnector = new GuessInputConnector {
    var index = 0
    override def getUserInput(): String = {
      val guess = guesses(index)
      index += 1
      guess
    }
  }

  "Wordle" should {
    "handle a player winning the game" when {
      "in all possible numbers of guesses" in {
        List(
          List(targetWord),
          List("CLEAN", targetWord),
          List("ELDER", "CLEAN", targetWord),
          List("HORSE", "ELDER", "CLEAN", targetWord),
          List("GREEN", "HORSE", "ELDER", "CLEAN", targetWord),
          List("HELLO", "GREEN", "HORSE", "ELDER", "CLEAN", targetWord)
        ).foreach { userInputGuesses =>
          withClue(s"for guesses $userInputGuesses") {
            val wordle = new Wordle(config, fileReader, chooseRandomWordF, guessConnector(userInputGuesses))
            wordle.startGame()
          }
        }
      }
    }

    "handle a player losing the game" when {
      "each guess is valid" in {
        val guesses = List("HELLO", "GREEN", "HORSE", "ELDER", "CLEAN", "CLOWN")
        val wordle  = new Wordle(config, fileReader, chooseRandomWordF, guessConnector(guesses))
        wordle.startGame()
      }

      "some guesses are the wrong number of letters, include special characters or are not valid words" in {
        val specialCharacterGuesses = List("&&&&&", "+++++", "%%%%%")
        val nonLetterGuesses        = List("12345", "ABC12")
        val wrongLengthGuesses      = List("", "ABC", "TESTING")
        val invalidWordGuesses      = List("AAAAA", "BBBBB", "CCCCC")
        val validGuesses            = List("HELLO", "GREEN", "HORSE", "ELDER", "CLEAN", "CLOWN")
        val invalidGuesses          = specialCharacterGuesses ::: nonLetterGuesses ::: wrongLengthGuesses ::: invalidWordGuesses
        val guesses                 = invalidGuesses ::: validGuesses
        val wordle                  = new Wordle(config, fileReader, chooseRandomWordF, guessConnector(guesses))
        wordle.startGame()
      }
    }
  }
}
