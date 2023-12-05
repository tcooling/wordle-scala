package com.tcooling.wordle.input

import com.tcooling.wordle.model.WordGuess

object UserInputGuessConnector extends GuessInputConnector {
  override def getUserInput(previousGuesses: List[WordGuess]): String = scala.io.StdIn.readLine()
}
