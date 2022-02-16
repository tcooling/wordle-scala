package com.tcooling.wordle.input

import com.tcooling.wordle.model.WordGuess

trait GuessInputConnector {

  /**
   * Get the guess from the user
   * @return the user input guess
   */
  def getUserInput(previousGuesses: List[WordGuess]): String

}
