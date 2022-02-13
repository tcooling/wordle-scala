package com.tcooling.wordle.model

import cats.implicits.toShow
import com.tcooling.wordle.model.LetterGuess.{CorrectGuess, IncorrectGuess, WrongPositionGuess}
import com.tcooling.wordle.model.WordGuess.showWordGuess
import org.scalatest.{Matchers, WordSpecLike}

final class WordGuessTest extends WordSpecLike with Matchers {

  private val targetWord: String = "VAGUE"

  "WordGuess" should {
    "be created from a target word and a user input word" when {
      "the words are the same" in {
        WordGuess(targetWord, targetWord) shouldBe WordGuess(targetWord.map(CorrectGuess).toList)
      }

      "some of the letters from the input word are in the target word, but in the wrong position" in {
        val wrongPositionLetters = "GRAIN"
        WordGuess(wrongPositionLetters, targetWord) shouldBe WordGuess(
          List(
            WrongPositionGuess('G'),
            IncorrectGuess('R'),
            WrongPositionGuess('A'),
            IncorrectGuess('I'),
            IncorrectGuess('N')
          )
        )
      }

      "none of the letters from the input word match the target word" in {
        val allLettersIncorrectGuess = "CLICK"
        WordGuess(allLettersIncorrectGuess, targetWord) shouldBe WordGuess(allLettersIncorrectGuess.map(IncorrectGuess).toList)
      }
    }

    "show a word guess" in {
      WordGuess(targetWord.map(IncorrectGuess).toList).show shouldBe targetWord
    }
  }

}
