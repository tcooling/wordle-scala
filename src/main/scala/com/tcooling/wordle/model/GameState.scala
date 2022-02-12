package com.tcooling.wordle.model

import cats.implicits.toShow
import com.tcooling.wordle.model.WordGuess.showWordGuess

/**
 * The Wordle game state, contains all the information required for a user (or computer) to make a guess
 */
final case class GameState(previousGuesses: List[WordGuess], numberOfGuessesRemaining: Int, targetWord: String) {
  def mostRecentGuess: Option[String] = previousGuesses.lastOption.map(_.show)
}
