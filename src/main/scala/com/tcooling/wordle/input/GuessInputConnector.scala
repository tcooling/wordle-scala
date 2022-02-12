package com.tcooling.wordle.input

import com.tcooling.wordle.model.GameState

trait GuessInputConnector {

  /**
   * Get the guess from the user
   * @param gameState the information needed to make a guess, e.g. what the previous guesses were
   * @return the user input guess
   */
  def getUserInput(gameState: GameState): String

}
