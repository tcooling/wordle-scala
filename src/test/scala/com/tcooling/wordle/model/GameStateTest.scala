package com.tcooling.wordle.model

import com.tcooling.wordle.model.LetterGuess.IncorrectGuess
import org.scalatest.{Matchers, WordSpecLike}

final class GameStateTest extends WordSpecLike with Matchers {

  "GameState" should {
    "get the most recent guess" when {
      "no guesses are present" in {
        GameState(previousGuesses = Nil, numberOfGuessesRemaining = 6, targetWord = "HELLO").mostRecentGuess shouldBe None
      }

      "one guess is present" in {
        val word      = "VAGUE"
        val wordGuess = WordGuess(word.map(IncorrectGuess).toList)
        GameState(previousGuesses = List(wordGuess), numberOfGuessesRemaining = 5, targetWord = "HELLO").mostRecentGuess shouldBe Some(word)
      }

      "multiple guesses are present" in {
        val words = Set("VAGUE", "HORSE", "CLICK", "PARTY")
        GameState(
          previousGuesses = words.map(word => WordGuess(word.map(IncorrectGuess).toList)).toList,
          numberOfGuessesRemaining = 2,
          targetWord = "HELLO"
        ).mostRecentGuess shouldBe words.lastOption
      }
    }
  }

}
