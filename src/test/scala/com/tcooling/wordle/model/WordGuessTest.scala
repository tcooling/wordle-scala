package com.tcooling.wordle.model

import cats.implicits.toShow
import com.tcooling.wordle.model.LetterGuess.IncorrectGuess
import com.tcooling.wordle.model.WordGuess.showWordGuess
import org.scalatest.{Matchers, WordSpecLike}

final class WordGuessTest extends WordSpecLike with Matchers {

  private val word: String = "VAGUE"

  "WordGuess" should {
    "show a word guess" in {
      WordGuess(word.map(IncorrectGuess).toList).show shouldBe word
    }
  }

}
