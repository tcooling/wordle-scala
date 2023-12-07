package com.tcooling.wordle.input

trait GuessInputConnector {

  /**
   * Get the guess from the user
   * @return
   *   the user input guess
   */
  def getUserInput: String

}
