package com.tcooling.wordle.model

import cats.implicits.toShow
import com.tcooling.wordle.model.LetterGuess.{CorrectGuess, IncorrectGuess, WrongPositionGuess}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class LetterGuessTest extends AnyWordSpecLike with Matchers {

  private val red:    String = Console.RED
  private val yellow: String = Console.YELLOW
  private val green:  String = Console.GREEN
  private val reset:  String = Console.RESET

  "LetterGuess" should {
    "show a letter in the corresponding colour" when {
      "the letter guess is incorrect" in {
        val lg: LetterGuess = IncorrectGuess('H')
        lg.show shouldBe (red + lg.letter + reset)
      }

      "the letter guess is in the wrong position" in {
        val lg: LetterGuess = WrongPositionGuess('H')
        lg.show shouldBe (yellow + lg.letter + reset)
      }

      "the letter guess is correct" in {
        val lg: LetterGuess = CorrectGuess('H')
        lg.show shouldBe (green + lg.letter + reset)
      }
    }
  }

}
