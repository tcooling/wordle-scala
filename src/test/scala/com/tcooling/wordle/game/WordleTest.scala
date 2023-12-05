package com.tcooling.wordle.game

import cats.data.NonEmptySet
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.WordleConfig
import com.tcooling.wordle.parser.{FileReader, WordsReader}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}

final class WordleTest extends WordSpecLike with Matchers with MockFactory {

  private val targetWord: String = "VAGUE"
  private val config: WordleConfig = WordleConfig(
    filename = "words.txt",
    wordLength = 5,
    numberOfGuesses = 6,
    useComputerGuessConnector = false
  )
  private val fileReader:        FileReader                    = WordsReader
  private val chooseRandomWordF: NonEmptySet[String] => String = _ => targetWord
  private val guessConnector:    GuessInputConnector           = mock[GuessInputConnector]

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
            val wordle = new Wordle(config, fileReader, chooseRandomWordF, (_, _) => guessConnector)
            mockUserInputGuesses(userInputGuesses)
            wordle.startGame()
          }
        }
      }
    }

    "handle a player losing the game" when {
      "each guess is valid" in {
        val wordle = new Wordle(config, fileReader, chooseRandomWordF, (_, _) => guessConnector)
        mockUserInputGuesses(List("HELLO", "GREEN", "HORSE", "ELDER", "CLEAN", "CLOWN"))
        wordle.startGame()
      }

      "some guesses are the wrong number of letters, include special characters or are not valid words" in {
        val wordle                  = new Wordle(config, fileReader, chooseRandomWordF, (_, _) => guessConnector)
        val specialCharacterGuesses = List("&&&&&", "+++++", "%%%%%")
        val nonLetterGuesses        = List("12345", "ABC12")
        val wrongLengthGuesses      = List("", "ABC", "TESTING")
        val invalidWordGuesses      = List("AAAAA", "BBBBB", "CCCCC")
        val validGuesses            = List("HELLO", "GREEN", "HORSE", "ELDER", "CLEAN", "CLOWN")
        val invalidGuesses          = specialCharacterGuesses ::: nonLetterGuesses ::: wrongLengthGuesses ::: invalidWordGuesses
        mockUserInputGuesses(invalidGuesses ::: validGuesses)
        wordle.startGame()
      }
    }
  }

  private def mockUserInputGuesses(guesses: List[String]): Unit =
    guesses.foreach((guessConnector.getUserInput _).expects(*).returns(_))

}
