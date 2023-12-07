package com.tcooling.wordle.input

object UserInputGuessConnector extends GuessInputConnector {
  override def getUserInput: String = scala.io.StdIn.readLine()
}
