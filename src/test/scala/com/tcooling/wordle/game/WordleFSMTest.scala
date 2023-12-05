package com.tcooling.wordle.game

import cats.data.{NonEmptyList, NonEmptySet}
import com.tcooling.wordle.input.GuessInputConnector
import com.tcooling.wordle.model.FSM.*
import com.tcooling.wordle.model.{FSM, WordGuess, WordleConfig}
import WordleFSM.State
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class WordleFSMTest extends AnyWordSpecLike with Matchers {

  private val wordleConfig: WordleConfig = WordleConfig(
    filename = "words.txt",
    wordLength = 5,
    numberOfGuesses = 6
  )

  private val word1:       String              = "HELLO"
  private val word2:       String              = "CLICK"
  private val word3:       String              = "HORSE"
  private val word4:       String              = "GREAT"
  private val word5:       String              = "ELDER"
  private val word6:       String              = "CLEAN"
  private val targetWord:  String              = "VAGUE"
  private val missingWord: String              = "ABCDE"
  private val allWords:    NonEmptySet[String] = NonEmptyList.of(targetWord, word1, word2, word3, word4, word5, word6).toNes

  private val wordGuessF: String => WordGuess = WordGuess(_, targetWord)

  private def withNextState(state: FSM, guesses: List[WordGuess], guess: String = ""): State =
    WordleFSM.nextState(
      config = wordleConfig,
      targetWord = targetWord,
      allWords = allWords,
      guessConnector = () => guess
    )(state, guesses)

  "WordleFSM" should {
    "return the next state and guesses for a state" when {
      "Start" in {
        withNextState(Start, Nil) shouldBe (PrintHelp, Nil)
      }

      "PrintHelp" in {
        withNextState(PrintHelp, Nil) shouldBe (PrintGameBoard, Nil)
      }

      "PrintGameBoard" in {
        withNextState(PrintGameBoard, Nil) shouldBe (CheckForWinOrLoss, Nil)
      }

      "CheckForWinOrLoss" when {
        "no guesses are present" in {
          withNextState(CheckForWinOrLoss, Nil) shouldBe (UserInputGuess, Nil)
        }

        "guesses are present" when {
          "all the guesses have been used" in {
            val guesses: List[WordGuess] = List(word1, word2, word3, word4, word5, word6).map(wordGuessF)
            withNextState(CheckForWinOrLoss, guesses) shouldBe (Lose, guesses)
          }

          "the first guess is correct" in {
            val guesses: List[WordGuess] = List(targetWord).map(wordGuessF)
            withNextState(CheckForWinOrLoss, guesses) shouldBe (Win, guesses)
          }

          "the last guess is correct" in {
            val guesses: List[WordGuess] = List(word1, word2, word3, word4, word5, targetWord).map(wordGuessF)
            withNextState(CheckForWinOrLoss, guesses) shouldBe (Win, guesses)
          }
        }
      }

      "UserInputGuess" when {
        "a valid guess is input" when {
          "the guess is in lowercase" in {
            withNextState(UserInputGuess, Nil, word1.toLowerCase) shouldBe (PrintGameBoard, List(word1).map(wordGuessF))
          }

          "the guess is in uppercase" in {
            withNextState(UserInputGuess, Nil, word1) shouldBe (PrintGameBoard, List(word1).map(wordGuessF))
          }

          "when the guess has already been input previously" in {
            withNextState(UserInputGuess, List(word1).map(wordGuessF), word1) shouldBe
              (PrintGameBoard, List(word1, word1).map(wordGuessF))
          }
        }

        "an invalid guess is input" when {
          "the guess is the wrong length" when {
            "empty" in {
              withNextState(UserInputGuess, Nil, guess = "") shouldBe (PrintGameBoard, Nil)
            }

            "too short" in {
              withNextState(UserInputGuess, Nil, word1.tail) shouldBe (PrintGameBoard, Nil)
            }

            "too long" in {
              withNextState(UserInputGuess, Nil, guess = s"${word1}A") shouldBe (PrintGameBoard, Nil)
            }
          }

          "the guess does not match the regex" when {
            "includes special characters" in {
              withNextState(UserInputGuess, Nil, guess = "&&&&&") shouldBe (PrintGameBoard, Nil)
            }

            "includes numbers" in {
              withNextState(UserInputGuess, Nil, guess = "12345") shouldBe (PrintGameBoard, Nil)
            }
          }

          "the guess does not correspond to a word in the words file" in {
            withNextState(UserInputGuess, Nil, missingWord) shouldBe (PrintGameBoard, Nil)
          }
        }
      }

      "Win" in {
        withNextState(Win, List(targetWord).map(wordGuessF)) shouldBe (Exit, List(targetWord).map(wordGuessF))
      }

      "Lose" in {
        withNextState(Lose, List(word1).map(wordGuessF)) shouldBe (Exit, List(word1).map(wordGuessF))
      }
    }
  }
}
