package com.tcooling.wordle.input

import com.tcooling.wordle.model.UserInputGuess

trait GuessInputConnector[F[_]] {

  /**
   * Get the guess from the user
   * @return
   *   the user input guess
   */
  def getUserInput: F[UserInputGuess.Type]

}
